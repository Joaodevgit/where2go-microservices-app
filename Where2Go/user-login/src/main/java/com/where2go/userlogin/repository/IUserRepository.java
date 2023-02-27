package com.where2go.userlogin.repository;

import com.where2go.userlogin.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user FROM User user WHERE user.email = ?1")
    Optional<User> findUserByEmail(String email);
}
