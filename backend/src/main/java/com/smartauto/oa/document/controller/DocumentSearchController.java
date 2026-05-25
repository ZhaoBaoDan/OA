package com.smartauto.oa.document.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.document.entity.DocumentSearchDoc;
import com.smartauto.oa.document.service.IDocumentSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档全文检索控制器
 */
@Tag(name = "文档全文检索")
@RestController
@RequestMapping("/document/search")
public class DocumentSearchController {

    private final IDocumentSearchService searchService;

    public DocumentSearchController(IDocumentSearchService searchService) {
        this.searchService = searchService;
    }

    @Operation(summary = "全文搜索文档")
    @GetMapping
    public Result<List<DocumentSearchDoc>> search(
            @Parameter(description = "关键词", required = true) @RequestParam String keyword) {
        return Result.success(searchService.search(keyword));
    }

    @Operation(summary = "按类型搜索文档")
    @GetMapping("/type")
    public Result<List<DocumentSearchDoc>> searchByType(
            @Parameter(description = "文件类型", required = true) @RequestParam String fileType) {
        return Result.success(searchService.searchByType(fileType));
    }
}
