package com.a606.common.util;

import com.a606.api.service.UserService;
import com.a606.db.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SudalUserDetailService implements UserDetailsService {

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByWallet(username);
        if(user != null) {
            SudalUserDetails userDetails = new SudalUserDetails(user);
            return userDetails;
        }
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
    }
}
