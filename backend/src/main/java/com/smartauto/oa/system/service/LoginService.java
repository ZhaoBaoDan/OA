package com.smartauto.oa.system.service;

import com.smartauto.oa.system.entity.LoginUser;

/**
 * 登录服务（供 SecurityConfig / JwtFilter 使用）
 */
public interface LoginService {

    /**
     * 根据用户ID从Redis获取登录用户信息
     */
    LoginUser getLoginUserById(Long userId);
}
