package com.a606.db.repository;

import com.a606.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findById(long userId);
    Optional<User> findByWallet(String wallet);

}
