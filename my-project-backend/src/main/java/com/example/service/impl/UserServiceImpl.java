package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.RestBean;
import com.example.entity.dto.UserDTO;
import com.example.entity.vo.response.LoginUserVO;
import com.example.mapper.UserMapper;
import com.example.service.UserService;

import com.example.utils.Constant;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
* @author sunjunchen
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-08-26 19:09:38
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDTO>
    implements UserService{



    @Resource
    private StringRedisTemplate template;
    @Resource
    private JwtUtils jwtUtils;

   @Override
    public UserDetails loadUserByUsername(String userAccount) throws UsernameNotFoundException {
        UserDTO userDTO = this.findUserByUserAccount(userAccount);
        if (userDTO == null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User.withUsername(userAccount)
                .password(userDTO.getUserPassword())
                .roles(userDTO.getUserRole()).build();
    }


    @Override
    public UserDTO findUserByUserAccount(String userAccount) {
        return this.lambdaQuery().eq(StringUtils.isNotBlank(userAccount),UserDTO::getUserAccount,userAccount).one();
    }

    @Override
    public RestBean<Long> register(String userAccount, String userPassword, String checkPassword) {
        //数据校验
        if (org.apache.commons.lang3.StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return RestBean.failure(400, "数据为空");
        }
        if (userAccount.length() < 4) {
            return RestBean.failure(400, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return RestBean.failure(400, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return RestBean.failure(400, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<UserDTO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.lambdaQuery().eq(UserDTO::getUserAccount,userAccount).count();
            if (count > 0) {
                return RestBean.failure(400, "用户名已被使用");
            }
            // 2. 加密
            String password =new BCryptPasswordEncoder().encode(userPassword);
            // 3. 插入数据
            UserDTO userDTO = UserDTO.builder().userAccount(userAccount).userPassword(password).build();
            boolean saveResult = this.save(userDTO);
            if (!saveResult) {
                return RestBean.failure(500, "注册失败，数据库错误");
            }
            return RestBean.success(userDTO.getId());
        }
    }

    @Override
    public RestBean<LoginUserVO> getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        String authorization = request.getHeader("Authorization");
        String token = jwtUtils.convertToken(authorization);
        String userAccount = template.opsForValue().get(Constant.LOGIN_USER + token);
        // 先判断是否已登录
//        Object userObj = request.getSession().getAttribute(Constant.LOGIN_USER);
//        User currentUser = (User) userObj;

        if (StringUtils.isEmpty(userAccount)) {
            return RestBean.failure(400, "用户未登录");
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
//        String username = currentUser.getUsername();
        UserDTO userDTO = this.findUserByUserAccount(userAccount);
        if (ObjectUtils.isEmpty(userDTO)) {
            return RestBean.failure(400, "用户未登录");
        }
        LoginUserVO loginUserVO = userDTO.asViewObject(LoginUserVO.class);
        return RestBean.success(loginUserVO);
    }
}




