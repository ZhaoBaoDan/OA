package com.smartauto.oa.document.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.document.entity.DocumentVersion;
import com.smartauto.oa.document.service.IDocumentVersionService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档版本控制器
 */
@Tag(name = "文档版本管理")
@RestController
@RequestMapping("/document")
public class DocumentVersionController {

    private final IDocumentVersionService versionService;

    public DocumentVersionController(IDocumentVersionService versionService) {
        this.versionService = versionService;
    }

    @Operation(summary = "文档版本列表")
    @GetMapping("/{documentId}/versions")
    public Result<List<DocumentVersion>> versions(@PathVariable Long documentId) {
        return Result.success(versionService.listByDocumentId(documentId));
    }

    @Operation(summary = "版本详情")
    @GetMapping("/version/{id}")
    public Result<DocumentVersion> getVersionById(@PathVariable Long id) {
        return Result.success(versionService.getById(id));
    }

    @OperLog(title = "上传文档新版本", businessType = "INSERT")
    @Operation(summary = "上传新版本")
    @PostMapping("/{documentId}/version")
    public Result<Void> createVersion(
            @PathVariable Long documentId,
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "版本说明") @RequestParam(required = false) String remark) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        versionService.createVersion(documentId, file, userId, userName, remark);
        return Result.success("版本上传成功");
    }
}
