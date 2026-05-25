package com.smartauto.oa.document.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.document.entity.BizDocument;
import com.smartauto.oa.document.service.IBizDocumentService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档管理
 */
@Tag(name = "文档管理")
@RestController
@RequestMapping("/document")
public class BizDocumentController {

    private final IBizDocumentService documentService;

    public BizDocumentController(IBizDocumentService documentService) {
        this.documentService = documentService;
    }

    @Operation(summary = "分页查询文档列表")
    @GetMapping
    public Result<PageResult<BizDocument>> page(BizDocument query,
                                                  @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                  @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(documentService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "根据ID查询文档")
    @GetMapping("/{id}")
    public Result<BizDocument> getById(@PathVariable Long id) {
        return Result.success(documentService.getById(id));
    }

    @OperLog(title = "上传文档", businessType = "INSERT")
    @Operation(summary = "上传文档")
    @PostMapping("/upload")
    public Result<Void> upload(@RequestParam("file") MultipartFile file,
                               @RequestParam(required = false) Long folderId) {
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        documentService.upload(file, folderId, userId, userName);
        return Result.success();
    }

    @OperLog(title = "删除文档", businessType = "DELETE")
    @Operation(summary = "删除文档")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        documentService.delete(ids);
        return Result.success();
    }
}
