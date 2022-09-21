package com.a606.api.service;

import com.a606.api.dto.MyNFTDto;
import com.a606.api.dto.UserDto;
import com.a606.db.entity.NFT;
import com.a606.db.entity.User;
import com.a606.db.repository.NFTRepository;
import com.a606.db.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NFTRepository nftRepository;
    @Autowired
    private ContractService contractService;

    private String getRandomNonce() {
        return String.valueOf((int)(Math.random()*1000000));
    }

    @Override
    public String getNonceByWallet(String userWallet) {
        Optional<User> oUser = userRepository.findByWallet(userWallet);
        if(oUser.isPresent()) {
            return oUser.get().getNonce();
        }
        return null;
    }

    @Override
    public User createUser(String userWallet) {
        User user = new User();
        user.setNickname("Unknown");
        user.setWallet(userWallet);
        user.setGamePoint(0l);
        user.setCreatedDate(LocalDateTime.now());
        user.setLastModifiedDate(LocalDateTime.now());
        user.setNonce(getRandomNonce());
        userRepository.save(user);
        return user;
    }


    @Override
    public List<MyNFTDto> getUserPageById(long userId) throws Exception {
        User user = userRepository.findById(userId).get();
        // web3를 통해서 solidity랑 통신해서 보유한 NFT의 tokenID를 얻어서
        // DB에서 tokenID로 검색한 NFT들을 List에 담아서 반환
        List<MyNFTDto> nfts = contractService.getNFTbyAddress(user.getWallet());
        for(MyNFTDto myNFT : nfts) {
            NFT nft = nftRepository.findByTokenId(myNFT.getTokenId()).get();
            myNFT.setId(nft.getId());
            myNFT.setLikeCount(nft.getLike_count());
            myNFT.setName(nft.getName());
            myNFT.setSaled(nft.is_saled());
        }

        return nfts;
    }

    @Override
    public Long getProfileById(long userId) throws Exception {
        User user = userRepository.findById(userId).get();
        NFT nft = nftRepository.findById(user.getProfile()).get();
        String address = contractService.getAddressbyTokenId(nft.getTokenId());
        if(address.equalsIgnoreCase(user.getWallet())){
            return user.getProfile();
        }
        return null;
    }

    @Override
    public User getUserByWallet(String userWallet) {
        Optional<User> user = userRepository.findByWallet(userWallet);
        return user.orElse(null);
    }

    @Override
    public User updateProfile(long userId, long nftTokenId) throws Exception {
        User user = userRepository.findById(userId).get();
        NFT nft = nftRepository.findById(nftTokenId).get();
        String address = contractService.getAddressbyTokenId(nft.getTokenId());
        if (!address.equalsIgnoreCase(user.getWallet())) {
            return null;
        }
        user.setProfile(nftTokenId);
        user = userRepository.save(user);

        return user;
    }

    @Override
    public String updateNickname(long userId, String nickname) {
        User user = userRepository.findById(userId).get();
        user.setNickname(nickname);
        user = userRepository.save(user);

        return user.getNickname();
    }

    @Override
    public void setNonce(long userId) {
        User user = userRepository.findById(userId).get();
        user.setNonce(getRandomNonce());
        userRepository.save(user);
    }
}
