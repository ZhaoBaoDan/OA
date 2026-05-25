package com.smartauto.oa.document.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.document.entity.DocumentFolder;
import com.smartauto.oa.document.service.IDocumentFolderService;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档文件夹控制器
 */
@Tag(name = "文档文件夹管理")
@RestController
@RequestMapping("/document/folder")
public class DocumentFolderController {

    private final IDocumentFolderService folderService;

    public DocumentFolderController(IDocumentFolderService folderService) {
        this.folderService = folderService;
    }

    @Operation(summary = "文件夹树形列表")
    @GetMapping("/tree")
    public Result<List<DocumentFolder>> tree() {
        return Result.success(folderService.tree());
    }

    @Operation(summary = "文件夹详情")
    @GetMapping("/{id}")
    public Result<DocumentFolder> getById(@PathVariable Long id) {
        return Result.success(folderService.getById(id));
    }

    @OperLog(title = "新增文件夹", businessType = "INSERT")
    @Operation(summary = "新增文件夹")
    @PostMapping
    public Result<Void> create(@RequestBody DocumentFolder folder) {
        folderService.create(folder);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改文件夹", businessType = "UPDATE")
    @Operation(summary = "修改文件夹")
    @PutMapping
    public Result<Void> update(@RequestBody DocumentFolder folder) {
        folderService.update(folder);
        return Result.success("修改成功");
    }

    @OperLog(title = "删除文件夹", businessType = "DELETE")
    @Operation(summary = "删除文件夹")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        folderService.delete(id);
        return Result.success("删除成功");
    }
}
