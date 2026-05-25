package com.smartauto.oa.attendance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 假期余额实体
 */
@Data
@TableName("leave_balance")
@Schema(description = "假期余额实体")
public class LeaveBalance implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "假期类型（annual年假 sick病假 personal事假 marriage婚假）")
    private String leaveType;

    @Schema(description = "总天数")
    private BigDecimal totalDays;

    @Schema(description = "已用天数")
    private BigDecimal usedDays;

    @Schema(description = "剩余天数")
    private BigDecimal remainDays;

    @Schema(description = "年份")
    private Integer year;

    @Schema(description = "删除标志")
    private Integer delFlag;

    @Schema(description = "创建者")
    private String createBy;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    private String updateBy;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
