package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.RestBean;
import com.example.entity.dto.UserDTO;
import com.example.entity.vo.response.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
* @author sunjunchen
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-08-26 19:09:38
*/
public interface UserService extends IService<UserDTO> , UserDetailsService {

    UserDTO findUserByUserAccount(String username);

    RestBean<Long> register(String userAccount, String userPassword, String checkPassword);

    RestBean<LoginUserVO> getLoginUser(HttpServletRequest request);

}
