package com.example.entity.vo.response;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @TableName user
 */
@Data
public class LoginUserVO implements Serializable {

    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private String token;

    private Date expire;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}