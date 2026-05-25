package com.smartauto.oa.system.service.impl;

import com.smartauto.oa.common.*;
import com.smartauto.oa.system.entity.LoginUser;
import com.smartauto.oa.system.entity.SysLoginLog;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.mapper.SysUserMapper;
import com.smartauto.oa.system.service.ISysAuthService;
import com.smartauto.oa.system.service.ISysLoginLogService;
import com.smartauto.oa.system.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Service
public class SysAuthServiceImpl implements ISysAuthService, LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final SysUserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ISysLoginLogService loginLogService;

    public SysAuthServiceImpl(AuthenticationManager authenticationManager,
                              JwtUtils jwtUtils,
                              SysUserMapper userMapper,
                              RedisTemplate<String, Object> redisTemplate,
                              ISysLoginLogService loginLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.redisTemplate = redisTemplate;
        this.loginLogService = loginLogService;
    }

    @Override
    public Map<String, String> login(String username, String password, String captchaCode, String captchaKey) {
        // 1. 验证码校验
        validateCaptcha(captchaCode, captchaKey);

        // 2. 检查登录失败次数限制
        checkLoginFailCount(username);

        // 3. 尝试认证
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException e) {
            // 登录失败，增加失败次数
            incrementLoginFailCount(username);
            // 记录登录失败日志
            recordLoginLog(username, 0, "密码错误");
            throw new BusinessException("用户名或密码错误");
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 4. 清除登录失败计数
        clearLoginFailCount(username);

        // 5. 生成 Token
        String accessToken = jwtUtils.generateToken(loginUser.getUserId(), loginUser.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(loginUser.getUserId(), loginUser.getUsername());

        // 6. 将用户信息存入 Redis
        String redisKey = Constants.REDIS_USER_PREFIX + loginUser.getUserId();
        redisTemplate.opsForValue().set(redisKey, loginUser, 2, TimeUnit.HOURS);

        // 7. 将 Token 存入 Redis（用于登出时校验）
        String tokenKey = Constants.REDIS_TOKEN_PREFIX + loginUser.getUserId();
        redisTemplate.opsForValue().set(tokenKey, accessToken, 2, TimeUnit.HOURS);

        // 8. 记录登录IP、登录时间到 sys_user 表
        String loginIp = getClientIp();
        SysUser updateUser = new SysUser();
        updateUser.setId(loginUser.getUserId());
        updateUser.setLoginIp(loginIp);
        updateUser.setLoginDate(LocalDateTime.now());
        userMapper.updateById(updateUser);

        // 9. 记录登录成功日志
        recordLoginLog(username, 1, "登录成功");

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        return tokenMap;
    }

    /**
     * 校验验证码
     */
    private void validateCaptcha(String captchaCode, String captchaKey) {
        if (captchaCode == null || captchaCode.isBlank()) {
            throw new BusinessException("验证码不能为空");
        }
        if (captchaKey == null || captchaKey.isBlank()) {
            throw new BusinessException("验证码Key不能为空");
        }

        String redisKey = Constants.REDIS_CAPTCHA_PREFIX + captchaKey;
        Object cachedCode = redisTemplate.opsForValue().get(redisKey);
        // 验证码使用后立即删除（一次性）
        redisTemplate.delete(redisKey);

        if (cachedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }
        if (!captchaCode.equalsIgnoreCase(cachedCode.toString())) {
            throw new BusinessException("验证码错误");
        }
    }

    /**
     * 检查登录失败次数
     */
    private void checkLoginFailCount(String username) {
        String redisKey = Constants.REDIS_LOGIN_FAIL_PREFIX + username;
        Object countObj = redisTemplate.opsForValue().get(redisKey);
        if (countObj != null) {
            int failCount = Integer.parseInt(countObj.toString());
            if (failCount >= Constants.MAX_LOGIN_FAIL_COUNT) {
                Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
                throw new BusinessException("账号已锁定，请" + (ttl != null ? ttl / 60 : 30) + "分钟后重试");
            }
        }
    }

    /**
     * 增加登录失败次数
     */
    private void incrementLoginFailCount(String username) {
        String redisKey = Constants.REDIS_LOGIN_FAIL_PREFIX + username;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            // 首次失败，设置过期时间
            redisTemplate.expire(redisKey, Constants.LOGIN_FAIL_LOCK_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * 清除登录失败次数
     */
    private void clearLoginFailCount(String username) {
        String redisKey = Constants.REDIS_LOGIN_FAIL_PREFIX + username;
        redisTemplate.delete(redisKey);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(String username, int status, String msg) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setIp(getClientIp());
            loginLog.setStatus(status);
            loginLog.setMsg(msg);
            loginLog.setLoginTime(LocalDateTime.now());

            // 解析 User-Agent
            HttpServletRequest request = getRequest();
            if (request != null) {
                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null) {
                    loginLog.setBrowser(parseBrowser(userAgent));
                    loginLog.setOs(parseOs(userAgent));
                }
            }
            loginLogService.recordLoginLog(loginLog);
        } catch (Exception e) {
            // 日志记录失败不影响登录流程
        }
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 解析浏览器类型
     */
    private String parseBrowser(String userAgent) {
        if (userAgent.contains("Edge")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) return "IE";
        return "Other";
    }

    /**
     * 解析操作系统
     */
    private String parseOs(String userAgent) {
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac OS")) return "MacOS";
        if (userAgent.contains("Linux")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "Other";
    }

    @Override
    public void logout() {
        Long userId = SecurityUtils.getUserId();
        if (userId != null) {
            // 删除 Redis 中的用户信息和 Token
            redisTemplate.delete(Constants.REDIS_USER_PREFIX + userId);
            redisTemplate.delete(Constants.REDIS_TOKEN_PREFIX + userId);
        }
    }

    @Override
    public LoginUser getCurrentUser() {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        return getLoginUserById(userId);
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(Result.UNAUTHORIZED_CODE, "刷新令牌无效");
        }

        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);

        // 检查用户是否仍然在 Redis 中（未登出）
        String redisKey = Constants.REDIS_USER_PREFIX + userId;
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(redisKey);
        if (loginUser == null) {
            throw new BusinessException(Result.UNAUTHORIZED_CODE, "用户已登出，请重新登录");
        }

        // 生成新的 Token
        String newAccessToken = jwtUtils.generateToken(userId, username);
        String newRefreshToken = jwtUtils.generateRefreshToken(userId, username);

        // 更新 Redis 中的 Token
        String tokenKey = Constants.REDIS_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(tokenKey, newAccessToken, 2, TimeUnit.HOURS);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);
        return tokenMap;
    }

    @Override
    public LoginUser getLoginUserById(Long userId) {
        // 先从 Redis 获取
        String redisKey = Constants.REDIS_USER_PREFIX + userId;
        LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(redisKey);
        if (loginUser != null) {
            return loginUser;
        }

        // Redis 中没有则从数据库加载
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getDelFlag() == 1) {
            return null;
        }

        loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickname(user.getNickname());
        loginUser.setDeptId(user.getDeptId());

        // 加载权限列表
        Set<String> permissions = new HashSet<>(userMapper.selectPermsByUserId(userId));
        loginUser.setPermissions(permissions);

        // 加载角色列表
        List<String> roleKeys = userMapper.selectRoleKeysByUserId(userId);
        loginUser.setRoleKeys(new HashSet<>(roleKeys));

        return loginUser;
    }
}
