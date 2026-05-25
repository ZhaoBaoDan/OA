package com.smartauto.oa.asset.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("biz_asset")
@Schema(description = "资产实体")
public class Asset implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "资产ID")
    private Long id;

    @NotBlank(message = "资产编码不能为空")
    @Schema(description = "资产编码")
    private String assetCode;

    @NotBlank(message = "资产名称不能为空")
    @Schema(description = "资产名称")
    private String name;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "规格型号")
    private String specification;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "采购日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @Schema(description = "采购价格")
    private BigDecimal purchasePrice;

    @Schema(description = "当前价值")
    private BigDecimal currentValue;

    @Schema(description = "折旧方法（straight/double）")
    private String depreciationMethod;

    @Schema(description = "折旧率(%)")
    private BigDecimal depreciationRate;

    @Schema(description = "存放位置")
    private String location;

    @Schema(description = "使用人ID")
    private Long userId;

    @Schema(description = "使用人姓名")
    private String userName;

    @Schema(description = "状态（0闲置 1在用 2维修 3报废）")
    private Integer status;

    @Schema(description = "供应商")
    private String supplier;

    @Schema(description = "质保到期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate warrantyDate;

    @Schema(description = "备注")
    private String remark;

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
