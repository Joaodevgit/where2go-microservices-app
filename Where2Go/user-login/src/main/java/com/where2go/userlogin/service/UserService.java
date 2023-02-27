package com.where2go.userlogin.service;

import com.where2go.userlogin.dto.User;
import com.where2go.userlogin.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    private final IUserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public UserDetails findUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if (!userOptional.isPresent())
            return null;

        final User user = userOptional.get();

        final UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
        );

        return userDetails;
    }

    public User findUserByEmailRepository (String email) {
        Optional<User> userOptional = userRepository.findUserByEmail(email);

        if (!userOptional.isPresent())
            return null;

        return userOptional.get();
    }

}
