package com.smartauto.oa.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartauto.oa.common.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_dict_data")
@Schema(description = "字典数据实体")
public class SysDictData extends BaseEntity {

    @NotBlank(message = "字典类型不能为空")
    @Schema(description = "字典类型")
    private String dictType;

    @NotBlank(message = "字典标签不能为空")
    @Schema(description = "字典标签")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    @Schema(description = "字典值")
    private String dictValue;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "状态（1正常 0停用）")
    private Integer status;

    @Schema(description = "样式属性")
    private String cssClass;

    @Schema(description = "备注")
    private String remark;
}
