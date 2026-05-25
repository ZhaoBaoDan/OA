package com.smartauto.oa.document.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档实体
 */
@Data
@TableName("biz_document")
@Schema(description = "文档实体")
public class BizDocument implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "文档ID")
    private Long id;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "文件类型（image/document/spreadsheet/pdf/other）")
    private String fileType;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件夹ID")
    private Long folderId;

    @Schema(description = "文件URL")
    private String fileUrl;

    @Schema(description = "存储路径")
    private String storagePath;

    @Schema(description = "上传人ID")
    private Long uploadBy;

    @Schema(description = "上传人名称")
    private String uploadName;

    @Schema(description = "删除标志")
    private Integer delFlag;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
