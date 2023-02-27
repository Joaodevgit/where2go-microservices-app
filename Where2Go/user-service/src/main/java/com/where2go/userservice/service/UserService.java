package com.where2go.userservice.service;

import com.where2go.userservice.dto.User;
import com.where2go.userservice.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
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

    public User getUserById(Long userId) {
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "User with id " + userId + " does not exist"));

        return user;
    }

    public User registerUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());

        if (userOptional.isPresent())
            throw new IllegalStateException("Email Taken");

        String encodedPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long userId, User user) {
        User userOptional = userRepository.findUserById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "User with id " + userId + " does not exist"));

        if (this.attributesIsNotNullAndHasContent(user.getFirstname()) &&
                !Objects.equals(userOptional.getFirstname(), user.getFirstname()))
            userOptional.setFirstname(user.getFirstname());

        if (this.attributesIsNotNullAndHasContent(user.getLastname()) &&
                !Objects.equals(userOptional.getLastname(), user.getLastname()))
            userOptional.setLastname(user.getLastname());

        if (this.attributesIsNotNullAndHasContent(user.getEmail()) &&
                !Objects.equals(userOptional.getEmail(), user.getEmail()))
            userOptional.setEmail(user.getEmail());

        String password = user.getPassword();
        String passwordEncoded = userOptional.getPassword();

        boolean isMatchPassword = passwordEncoder.matches(password, passwordEncoded);

        if (this.attributesIsNotNullAndHasContent(password) && !isMatchPassword) {
            String encodedPassword = this.passwordEncoder.encode(password);
            userOptional.setPassword(encodedPassword);
        }

        return userOptional;
    }

    public void deleteUser(Long id){
        boolean exist = userRepository.existsById(id);

        if(!exist) {
            throw new IllegalStateException("User With id "+id+" does not exists");
        }

        userRepository.deleteById(id);
    }

    private boolean attributesIsNotNullAndHasContent(String content) {
        return content != null && content.length() > 0;
    }

}
