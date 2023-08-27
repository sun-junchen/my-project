package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.UserDTO;
import com.example.entity.vo.response.UserVO;
import com.example.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @Resource
    private UserService userService;
    /**
     *
     * @return 假数据
     */
    @GetMapping("/userList")
    public RestBean<List<UserVO>> hello(){
        List<UserDTO> list = userService.lambdaQuery().list();
        List<UserVO> list1 = list.stream().map(v -> v.asViewObject(UserVO.class)).toList();
        return RestBean.success(list1);
    }


}
