package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.LoginUserVO;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {



    @Resource
    private UserService userService;
    @GetMapping("/getLoginUser")
    public RestBean<LoginUserVO> getLoginUser(HttpServletRequest request){
        return userService.getLoginUser(request);
    }


}
