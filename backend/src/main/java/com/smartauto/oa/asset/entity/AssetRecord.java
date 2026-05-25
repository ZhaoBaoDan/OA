package com.smartauto.oa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("biz_asset_record")
@Schema(description = "资产流转记录")
public class AssetRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "资产ID")
    private Long assetId;

    @Schema(description = "类型（purchase/instock/assign/return/repair/scrap）")
    private String type;

    @Schema(description = "操作人ID")
    private Long operatorId;

    @Schema(description = "操作人姓名")
    private String operatorName;

    @Schema(description = "原使用人ID")
    private Long fromUserId;

    @Schema(description = "新使用人ID")
    private Long toUserId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "操作时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime operateTime;
}
