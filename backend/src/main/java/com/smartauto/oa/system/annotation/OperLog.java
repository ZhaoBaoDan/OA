package com.smartauto.oa.system.annotation;

import java.lang.annotation.*;

/**
 * 操作日志自定义注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /**
     * 模块标题
     */
    String title() default "";

    /**
     * 操作类型（INSERT, UPDATE, DELETE, EXPORT, IMPORT, OTHER）
     */
    String businessType() default "OTHER";
}
