package com.smartauto.oa.system.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysOperLog;
import com.smartauto.oa.system.service.ISysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 */
@Aspect
@Component
public class OperLogAspect {

    private static final Logger log = LoggerFactory.getLogger(OperLogAspect.class);

    private final ISysOperLogService operLogService;
    private final ObjectMapper objectMapper;

    public OperLogAspect(ISysOperLogService operLogService, ObjectMapper objectMapper) {
        this.operLogService = operLogService;
        this.objectMapper = objectMapper;
    }

    /**
     * 配置切入点：所有标注了 @OperLog 注解的方法
     */
    @Pointcut("@annotation(com.smartauto.oa.system.annotation.OperLog)")
    public void operLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取注解信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        OperLog operLogAnnotation = method.getAnnotation(OperLog.class);

        SysOperLog operLog = new SysOperLog();
        operLog.setTitle(operLogAnnotation.title());
        operLog.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
        operLog.setOperTime(LocalDateTime.now());

        // 获取请求信息
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.setRequestUrl(request.getRequestURI());
                operLog.setOperIp(getClientIp(request));
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败: {}", e.getMessage());
        }

        // 获取请求参数（排除文件类型）
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String params = objectMapper.writeValueAsString(args);
                // 限制参数长度
                if (params.length() > 2000) {
                    params = params.substring(0, 2000);
                }
                operLog.setRequestParam(params);
            }
        } catch (Exception e) {
            log.warn("序列化请求参数失败: {}", e.getMessage());
        }

        // 获取当前操作人
        try {
            com.smartauto.oa.system.entity.LoginUser loginUser = com.smartauto.oa.common.SecurityUtils.getLoginUser();
            if (loginUser != null) {
                operLog.setOperName(loginUser.getUsername());
            }
        } catch (Exception e) {
            // 未登录状态不设置操作人
        }

        Object result = null;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            operLog.setStatus(1); // 成功

            // 记录返回结果（仅记录简单类型）
            try {
                if (result != null) {
                    String resultStr = objectMapper.writeValueAsString(result);
                    if (resultStr.length() > 2000) {
                        resultStr = resultStr.substring(0, 2000);
                    }
                    operLog.setResponseResult(resultStr);
                }
            } catch (Exception ignored) {
            }

        } catch (Throwable e) {
            operLog.setStatus(0); // 失败
            operLog.setErrorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算耗时
            long costTime = System.currentTimeMillis() - startTime;
            operLog.setCostTime(costTime);

            // 异步保存操作日志
            try {
                operLogService.insert(operLog);
            } catch (Exception e) {
                log.error("保存操作日志失败: {}", e.getMessage());
            }
        }

        return result;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
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
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
        return ip;
    }
}
