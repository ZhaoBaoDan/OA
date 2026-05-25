package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.LoginUser;
import com.smartauto.oa.system.service.ISysAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证管理
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class SysAuthController {

    private final ISysAuthService authService;

    public SysAuthController(ISysAuthService authService) {
        this.authService = authService;
    }

    @OperLog(title = "用户登录", businessType = "OTHER")
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String captchaCode = params.get("captchaCode");
        String captchaKey = params.get("captchaKey");
        Map<String, String> tokens = authService.login(username, password, captchaCode, captchaKey);
        return Result.success("登录成功", tokens);
    }

    @OperLog(title = "用户登出", businessType = "OTHER")
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<LoginUser> info() {
        LoginUser loginUser = authService.getCurrentUser();
        return Result.success(loginUser);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<Map<String, String>> refresh(@Parameter(description = "刷新令牌", required = true) @RequestParam String refreshToken) {
        Map<String, String> tokens = authService.refreshToken(refreshToken);
        return Result.success("刷新成功", tokens);
    }
}
