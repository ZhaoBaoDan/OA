package com.smartauto.oa.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档文件夹实体
 */
@Data
@TableName("document_folder")
@Schema(description = "文档文件夹实体")
public class DocumentFolder implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "文件夹名称不能为空")
    @Schema(description = "文件夹名称")
    private String name;

    @Schema(description = "父文件夹ID")
    private Long parentId;

    @Schema(description = "路径")
    private String path;

    @Schema(description = "创建人ID")
    private Long creatorId;

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

    @TableField(exist = false)
    @Schema(description = "子文件夹")
    private List<DocumentFolder> children;
}
