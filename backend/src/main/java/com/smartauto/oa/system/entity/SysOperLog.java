package com.smartauto.oa.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志实体
 */
@Data
@TableName("sys_oper_log")
@Schema(description = "操作日志实体")
public class SysOperLog implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "日志ID")
    private Long id;

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "方法名称")
    private String method;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求参数")
    private String requestParam;

    @Schema(description = "返回结果")
    private String responseResult;

    @Schema(description = "操作状态（1正常 0失败）")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "操作IP")
    private String operIp;

    @Schema(description = "操作人")
    private String operName;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operTime;

    @Schema(description = "耗时(ms)")
    private Long costTime;
}
