package com.by.open.ontology.userservice.controller;

import com.by.open.ontology.userservice.entity.User;
import com.by.open.ontology.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return userService.login(user.getUsername(), user.getPassword());
    }

    @GetMapping("/findByUsername")
    public User findByUsername(@RequestParam String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/findByEmail")
    public User findByEmail(@RequestParam String email) {
        return userService.findByEmail(email);
    }

    @GetMapping("/findAll")
    public List<User> findAll() {
        return userService.findAll();
    }

    @PutMapping("/update")
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }

    @PostMapping("/change-email")
    public User changeEmail(@RequestBody ChangeEmailRequest request) {
        return userService.changeEmail(request.getUsername(), request.getNewEmail(), request.getPassword());
    }

    @PostMapping("/change-password")
    public User changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request.getUsername(), request.getCurrentPassword(), request.getNewPassword());
    }
}