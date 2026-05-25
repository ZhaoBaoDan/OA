package com.smartauto.oa.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 岗位实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_post")
@Schema(description = "岗位实体")
public class SysPost extends BaseEntity {

    @NotBlank(message = "岗位编码不能为空")
    @Schema(description = "岗位编码")
    private String postCode;

    @NotBlank(message = "岗位名称不能为空")
    @Schema(description = "岗位名称")
    private String postName;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（1正常 0停用）")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
