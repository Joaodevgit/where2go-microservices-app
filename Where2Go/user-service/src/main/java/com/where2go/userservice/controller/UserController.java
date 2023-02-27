package com.where2go.userservice.controller;

import com.where2go.userservice.dto.User;
import com.where2go.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return this.userService.getUsers();
    }

    @GetMapping("{userId}")
    public User getUsersById(@PathVariable("userId") Long userId) {
        return this.userService.getUserById(userId);
    }

    @PostMapping
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @PutMapping("{userId}")
    public ResponseEntity<User> registerUser(@PathVariable("userId") Long userId, @RequestBody User user) {
        User userUpdate = userService.updateUser(userId, user);

        return ResponseEntity.ok(userUpdate);
    }

    @DeleteMapping("{userId}")
    public void registerUser(@PathVariable("userId") Long userId) {
        this.userService.deleteUser(userId);
    }
}
