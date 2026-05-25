package com.smartauto.oa.system.service;

import com.smartauto.oa.system.entity.LoginUser;

import java.util.Map;

/**
 * 认证服务接口
 */
public interface ISysAuthService {

    /**
     * 用户登录（带验证码校验）
     *
     * @param username    用户名
     * @param password    密码
     * @param captchaCode 验证码
     * @param captchaKey  验证码Key
     * @return token 信息（accessToken, refreshToken）
     */
    Map<String, String> login(String username, String password, String captchaCode, String captchaKey);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     *
     * @return LoginUser
     */
    LoginUser getCurrentUser();

    /**
     * 刷新Token
     *
     * @param refreshToken 刷新令牌
     * @return 新的token信息
     */
    Map<String, String> refreshToken(String refreshToken);
}
