package com.a606.api.controller;

import com.a606.api.dto.MyNFTDto;
import com.a606.api.dto.UserDto;
import com.a606.api.service.ContractService;
import com.a606.api.service.UserService;
import com.a606.common.util.JwtTokenUtil;
import com.a606.common.util.SudalUserDetails;
import com.a606.db.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Api(value = "유저 API", tags = {"User"})
@RestController
@RequestMapping("api/")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    @GetMapping("user/nonce/{address}")
    @ApiOperation(value = "로그인 nonce, 회원 가입", notes = "지갑주소가 있으면 로그인 없으면 회원가입 후 nonce 반환")
    public ResponseEntity<String> loginNonce(@PathVariable String address){
        String nonce = userService.getNonceByWallet(address);
        // 지갑 주소가 없으면 회원가입
        if(nonce == null) {
            User user = userService.createUser(address);
            nonce = user.getNonce();
            return new ResponseEntity<>(nonce, HttpStatus.CREATED);
        }

        return new ResponseEntity<>(nonce, HttpStatus.OK);
    }

    @PostMapping("user")
    @ApiOperation(value = "로그인", notes = "jwt 토큰 리턴")
//    @ApiResponse({
//            @ApiResponse(code = 201, message = "성공"),
//            @ApiResponse(code = 500, message = "서버 오류")
//    })
    public ResponseEntity<?> login(@RequestBody @ApiParam(value = "로그인 정보", required = true) @Valid UserDto.LoginRequest loginInfo, Model model, HttpSession session,
                                   HttpServletResponse response) {
        String userWallet = loginInfo.getWallet();
        String message = loginInfo.getMessage();

        User user = userService.getUserByWallet(userWallet);
        if (user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        String nonce = user.getNonce();

        System.out.println("nonce : " + nonce);

        byte[] nonceHash = Hash.sha3(nonce.getBytes());
        byte[] signatureBytes = Numeric.hexStringToByteArray(message);

        byte v = (byte) ((signatureBytes[64] < 27) ? (signatureBytes[64] + 27) : signatureBytes[64]);
        byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);

        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);

        List<String> recoveredKeys = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            BigInteger _r = new BigInteger(1, signatureData.getR());
            BigInteger _s = new BigInteger(1, signatureData.getS());
            ECDSASignature ecdsaSignature = new ECDSASignature(_r, _s);
            BigInteger recoveredKey = Sign.recoverFromSignature((byte)i, ecdsaSignature, nonceHash);
            if(recoveredKey != null) {
                recoveredKeys.add("0x" + Keys.getAddress(recoveredKey));
            }
        }

        for(String recoveredKey : recoveredKeys) {
            if(recoveredKey.equalsIgnoreCase(userWallet)) {
                userService.setNonce(user.getId());
                String jwtToken = JwtTokenUtil.getToken(userWallet);
                return ResponseEntity.status(200).body(jwtToken);
            }
        }

        return ResponseEntity.status(400).body(null);
    }

    @GetMapping("user/mynft")
    @ApiOperation(value = "보유 nft 조회", notes = "로그인 한 계정의 보유 nft 조회")
    public ResponseEntity<List<MyNFTDto>> myPage(@ApiIgnore Authentication authentication) throws Exception {
        if (authentication == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        User user = ((SudalUserDetails) authentication.getDetails()).getUser();
        List<MyNFTDto> nfts = userService.getUserPageById(user.getId());

        return new ResponseEntity<>(nfts, HttpStatus.OK);
    }

    //blockchain - 소유 아니면 defalut로 변경
    @GetMapping("user/profile/")
    @ApiOperation(value = "프로필 조회", notes = "로그인 한 계정의 프로필인 NFT id 리턴")
    public ResponseEntity<Long> getProfile(@ApiIgnore Authentication authentication) throws Exception {
        if (authentication == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        User user = ((SudalUserDetails) authentication.getDetails()).getUser();
        Long profile = userService.getProfileById(user.getId());
        // profile이 보유한 nft가 아닐 때
        if(profile == null){
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    //updateProfile - blockchain
    @PostMapping("user/change/profile")
    public ResponseEntity<?> updateProfile(@ApiIgnore Authentication authentication,
            @RequestBody @ApiParam(value = "프로필 변경", required = true) UserDto.updateProfileRequest updateProfileRequest) throws Exception {
        if (authentication == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        User user = ((SudalUserDetails) authentication.getDetails()).getUser();
        user = userService.updateProfile(user.getId(), updateProfileRequest.getNftId());
        if (user == null){
           return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //updateNickname
    @PostMapping("user/change/nickname")
    public ResponseEntity<String> updateNickname(@ApiIgnore Authentication authentication,
            @RequestBody @ApiParam(value = "프로필 변경", required = true) UserDto.updateNicknameRequest updateNicknameRequest) {
        if (authentication == null) return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        User user = ((SudalUserDetails) authentication.getDetails()).getUser();
        return new ResponseEntity<>(
                userService.updateNickname(user.getId(), updateNicknameRequest.getNickname()), HttpStatus.OK);
    }
}
