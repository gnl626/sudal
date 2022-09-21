package com.a606.api.service;

import com.a606.api.dto.MyNFTDto;
import com.a606.api.dto.UserDto;
import com.a606.db.entity.NFT;
import com.a606.db.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface UserService {

    @Transactional
    String getNonceByWallet(String userWallet);

    @Transactional
    User createUser(String userWallet);

    @Transactional
    User getUserByWallet(String userWallet);

    @Transactional
    List<MyNFTDto> getUserPageById(long userId) throws Exception;

    @Transactional
    Long getProfileById(long userId) throws Exception;

    @Transactional
    User updateProfile(long userId, long nftTokenId) throws Exception;

    @Transactional
    String updateNickname(long userId, String nickname);

    @Transactional
    void setNonce(long userId);

}
