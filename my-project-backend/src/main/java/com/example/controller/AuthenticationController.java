package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public RestBean<Long> userRegister(String userAccount,
                                       String userPassword,
                                       String checkPassword) {
      return userService.register(userAccount,userPassword,checkPassword);
    }
}
