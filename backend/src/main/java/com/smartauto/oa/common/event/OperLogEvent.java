package com.smartauto.oa.common.event;

import lombok.Data;

import java.io.Serializable;

/**
 * 操作日志事件
 */
@Data
public class OperLogEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模块标题 */
    private String title;

    /** 方法名称 */
    private String method;

    /** 请求 URL */
    private String requestUrl;

    /** 请求参数 */
    private String requestParam;

    /** 返回结果 */
    private String responseResult;

    /** 操作状态（1正常 0失败） */
    private Integer status;

    /** 错误消息 */
    private String errorMsg;

    /** 操作 IP */
    private String operIp;

    /** 操作人 */
    private String operName;

    /** 耗时(ms) */
    private Long costTime;

    public OperLogEvent() {}
}
