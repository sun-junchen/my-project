package com.example.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.entity.RestBean;
import com.example.entity.dto.UserDTO;
import com.example.entity.vo.response.LoginUserVO;
import com.example.filter.JwtAuthorizeFilter;
import com.example.service.UserService;
import com.example.utils.Constant;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {


    @Resource
    private JwtUtils jwtUtils;
    @Resource
    JwtAuthorizeFilter jwtAuthorizeFilter;

    @Resource
    UserService userService;


    @Resource
    private StringRedisTemplate template;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(conf -> conf.requestMatchers("/api/auth/**").permitAll().anyRequest().authenticated()
                ).formLogin(conf -> conf.loginProcessingUrl("/api/auth/login").successHandler(
                        this::handleProcess).failureHandler(this::handleProcess))
                .logout(conf -> conf.logoutUrl("/api/auth/logout").logoutSuccessHandler(this::onLogoutSuccess))
                .exceptionHandling(conf -> conf.authenticationEntryPoint(this::handleProcess)
                        .accessDeniedHandler(this::handleProcess))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthorizeFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    public void handleProcess(HttpServletRequest request,
                              HttpServletResponse response,
                              Object exceptionOrAuthentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        if (exceptionOrAuthentication instanceof AccessDeniedException accessDeniedException) {
            writer.write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Exception exception) {
            writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
        } else if (exceptionOrAuthentication instanceof Authentication authentication) {
            User user = (User) authentication.getPrincipal();
            UserDTO userDTO = userService.findUserByUserAccount(user.getUsername());
            String token = jwtUtils.createJwt(user, userDTO.getId(), userDTO.getUserAccount());
            if (StringUtils.isBlank(token)) {
                writer.write(RestBean.forbidden("登录验证频繁,请稍后再试").asJsonString());
            } else {
                LoginUserVO loginUserVO = userDTO.asViewObject(LoginUserVO.class, v -> {
                    v.setExpire(jwtUtils.expireTime());
                    v.setToken(token);
                });
                template.opsForValue().set(Constant.LOGIN_USER + token,loginUserVO.getUserAccount());
//                request.getSession().setAttribute(Constant.LOGIN_USER, loginUserVO);
                writer.write(RestBean.success(loginUserVO).asJsonString());
            }
        }
    }

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (jwtUtils.invalidateJwt(authorization)) {
            writer.write(RestBean.success().asJsonString());
        } else {
            writer.write(RestBean.failure(400, "退出登录失败").asJsonString());
        }

    }
}