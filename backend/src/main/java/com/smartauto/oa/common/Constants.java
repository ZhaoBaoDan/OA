package com.smartauto.oa.common;

/**
 * 系统常量
 */
public class Constants {

    /** 超级管理员角色标识 */
    public static final String ADMIN_ROLE = "admin";

    /** 正常状态 */
    public static final String STATUS_NORMAL = "1";

    /** 停用状态 */
    public static final String STATUS_DISABLE = "0";

    /** 菜单类型 - 目录 */
    public static final String MENU_TYPE_DIR = "M";

    /** 菜单类型 - 菜单 */
    public static final String MENU_TYPE_MENU = "C";

    /** 菜单类型 - 按钮 */
    public static final String MENU_TYPE_BUTTON = "B";

    /** JWT Token前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 用户ID字段 */
    public static final String CLAIM_USER_ID = "userId";

    /** 用户名字段 */
    public static final String CLAIM_USERNAME = "username";

    /** Redis Token前缀 */
    public static final String REDIS_TOKEN_PREFIX = "token:";

    /** Redis 用户信息前缀 */
    public static final String REDIS_USER_PREFIX = "user_info:";

    /** Redis 字典缓存前缀 */
    public static final String REDIS_DICT_PREFIX = "dict:";

    /** Token 过期时间（毫秒）- 默认2小时 */
    public static final long TOKEN_EXPIRATION = 7200000L;

    /** Refresh Token 过期时间（毫秒）- 默认24小时 */
    public static final long REFRESH_TOKEN_EXPIRATION = 86400000L;

    /** 排序方向 - 升序 */
    public static final String ORDER_ASC = "asc";

    /** 排序方向 - 降序 */
    public static final String ORDER_DESC = "desc";

    /** Redis 验证码前缀 */
    public static final String REDIS_CAPTCHA_PREFIX = "captcha:";

    /** Redis 登录失败次数前缀 */
    public static final String REDIS_LOGIN_FAIL_PREFIX = "login_fail:";

    /** 最大登录失败次数 */
    public static final int MAX_LOGIN_FAIL_COUNT = 5;

    /** 登录失败锁定时间（秒）- 30分钟 */
    public static final int LOGIN_FAIL_LOCK_SECONDS = 1800;

    /** 验证码过期时间（秒）- 5分钟 */
    public static final int CAPTCHA_EXPIRE_SECONDS = 300;
}
