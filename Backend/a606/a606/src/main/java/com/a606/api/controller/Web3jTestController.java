package com.a606.api.controller;

import com.a606.contract.SudalFarm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Api(value = "Web3j API", tags = {"Web3j."})
@RestController
@RequestMapping("/api/web3j")
public class Web3jTestController {
    /*
     web3j 테스트용 컨트롤러
     Mapping 막 했으니 신경쓰지 말기

    HttpService : 블록체인 네트워크 주소 넣기

     변수 설명
     contract : 배포한 컨트랙트 주소
     key : 팀 지갑 PRIVATE KEY
     */

    @PostMapping()
    @ApiOperation(value = "Web3j 테스트", notes = "Web3j 테스트")
    @ApiResponses({
            @ApiResponse(code = 201, message = "성공"),
            @ApiResponse(code = 401, message = "unauthenticated"),
            @ApiResponse(code = 403, message = "unauthorized")
    })
    public List<String> web3ClientVersion() throws IOException {
        Admin admin = Admin.build(new HttpService("http://20.196.209.2:8545"));
        PersonalListAccounts personalListAccounts = admin.personalListAccounts().send();
        List<String> addressList = personalListAccounts.getAccountIds();

        return addressList;
    }

    @GetMapping()
    @ApiOperation(value = "Web3j 체인 iD", notes = "Web3j 테스트")
    @ApiResponses({
            @ApiResponse(code = 201, message = "성공"),
            @ApiResponse(code = 401, message = "unauthenticated"),
            @ApiResponse(code = 403, message = "unauthorized")
    })
    public String getWeb3ChainID() throws IOException {
        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));

        return web3j.netVersion().send().getNetVersion();
    }

    @PostMapping("/contract")
    @ApiOperation(value = "Web3j contract get sudals by owner", notes = "Web3j 테스트")
    @ApiResponses({
            @ApiResponse(code = 201, message = "성공"),
            @ApiResponse(code = 401, message = "unauthenticated"),
            @ApiResponse(code = 403, message = "unauthorized")
    })
    public ResponseEntity<?> getWeb3Contract(@RequestParam String address) throws Exception {
        String contract = "0x71fEBBe570f91C1bc3711807A9041c19DcD98A3c";     // 배포한 컨트랙트 주소
        String key = "583705a3b516ea8bc8672540ffdd9e2806e23c48e89e1fb4fa862242937e0ef1";

        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        Credentials credentials = Credentials.create(key);
        ContractGasProvider gasProvider = new DefaultGasProvider();
        FastRawTransactionManager manager = new FastRawTransactionManager(web3j, credentials,
                new PollingTransactionReceiptProcessor(web3j, 3000, 3));

        SudalFarm sudalFarm = SudalFarm.load(contract, web3j, manager, gasProvider);

        Tuple3 tuple3 = sudalFarm.getSudalsByOwner(address).send();

        List<String> names = (List<String>) tuple3.component1();
        List<BigInteger> ids = (List<BigInteger>) tuple3.component2();
        List<BigInteger> dnas = (List<BigInteger>) tuple3.component3();

        System.out.println(names);
        System.out.println(ids);
        System.out.println(dnas);

        return ResponseEntity.status(200).body(tuple3);
    }

    @GetMapping("/contract")
    @ApiOperation(value = "Web3j contract get sudals", notes = "Web3j 테스트")
    @ApiResponses({
            @ApiResponse(code = 201, message = "성공"),
            @ApiResponse(code = 401, message = "unauthenticated"),
            @ApiResponse(code = 403, message = "unauthorized")
    })
    public ResponseEntity<?> getWeb3SudalsByOwner() throws Exception {
//        String contract = "0xCC7e82114aF80c36Bb815a92BF85a6A8f9155188";
//        String key = "2c835aeb997e4d1269513a83efadc44de96a30a56c1b58cb7f8ce8897511c79f";

//        Web3j web3j = Web3j.build(new HttpService("http://20.196.209.2:8545"));
        String contract = "0x71fEBBe570f91C1bc3711807A9041c19DcD98A3c";
        String key = "7de9b804253e76a421515313536b138ca1addd3e9aaaf07152d9155b71edaafa";

        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        Credentials credentials = Credentials.create(key);
        ContractGasProvider gasProvider = new DefaultGasProvider();
        FastRawTransactionManager manager = new FastRawTransactionManager(web3j, credentials,
                new PollingTransactionReceiptProcessor(web3j, 3000, 3));

        SudalFarm sudalFarm = SudalFarm.load(contract, web3j, manager, gasProvider);
        Tuple3 tuple3 = sudalFarm.getSudals().send();

        List<String> names = (List<String>) tuple3.component1();
        List<BigInteger> ids = (List<BigInteger>) tuple3.component2();
        List<BigInteger> dnas = (List<BigInteger>) tuple3.component3();

        System.out.println(names);
        System.out.println(ids);
        System.out.println(dnas);

        return ResponseEntity.status(200).body(tuple3);
    }

    @PostMapping("/nft")
    @ApiOperation(value = "Web3j contract create sudal nft", notes = "Web3j 테스트")
    @ApiResponses({
            @ApiResponse(code = 201, message = "성공"),
            @ApiResponse(code = 401, message = "unauthenticated"),
            @ApiResponse(code = 403, message = "unauthorized")
    })
    public ResponseEntity<?> createNFT(@RequestParam String sudalName, String sudalDNA) throws Exception {
        String contract = "0x71fEBBe570f91C1bc3711807A9041c19DcD98A3c";
        String key = "7de9b804253e76a421515313536b138ca1addd3e9aaaf07152d9155b71edaafa";

        Web3j web3j = Web3j.build(new HttpService("http://localhost:7545"));
        Credentials credentials = Credentials.create(key);
        ContractGasProvider gasProvider = new StaticGasProvider(BigInteger.valueOf(22_000_000_000L), BigInteger.valueOf(4_300_000));
        FastRawTransactionManager manager = new FastRawTransactionManager(web3j, credentials,
                new PollingTransactionReceiptProcessor(web3j, 3000, 3));

        SudalFarm sudalFarm = SudalFarm.load(contract, web3j, manager, gasProvider);

        System.out.println(sudalFarm._createSudal(sudalName, new BigInteger(sudalDNA)).send());

        return ResponseEntity.status(200).body("success");
    }
}
