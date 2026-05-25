package com.smartauto.oa.common;

import com.smartauto.oa.system.entity.LoginUser;
import com.smartauto.oa.system.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final LoginService loginService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, LoginService loginService) {
        this.jwtUtils = jwtUtils;
        this.loginService = loginService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头中获取Token
        String bearerToken = request.getHeader(jwtUtils.getHeader());
        String token = jwtUtils.resolveToken(bearerToken);

        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 解析用户信息
            Long userId = jwtUtils.getUserIdFromToken(token);
            // 从Redis获取用户完整信息
            LoginUser loginUser = loginService.getLoginUserById(userId);

            if (loginUser != null) {
                // 设置认证信息到SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
