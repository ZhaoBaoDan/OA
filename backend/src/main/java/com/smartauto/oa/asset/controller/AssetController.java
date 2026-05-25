package com.smartauto.oa.asset.controller;

import com.smartauto.oa.asset.entity.Asset;
import com.smartauto.oa.asset.service.AssetService;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 资产管理控制器
 */
@Tag(name = "资产管理")
@RestController
@RequestMapping("/asset")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @Operation(summary = "资产列表")
    @GetMapping
    public Result<PageResult<Asset>> list(Asset query,
                                          @RequestParam(defaultValue = "1") int pageNum,
                                          @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(assetService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "资产详情")
    @GetMapping("/{id}")
    public Result<Asset> getById(@PathVariable Long id) {
        return Result.success(assetService.getById(id));
    }

    @OperLog(title = "新增资产", businessType = "INSERT")
    @Operation(summary = "新增资产")
    @PostMapping
    public Result<Void> create(@RequestBody Asset asset) {
        assetService.create(asset);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改资产", businessType = "UPDATE")
    @Operation(summary = "修改资产")
    @PutMapping
    public Result<Void> update(@RequestBody Asset asset) {
        assetService.update(asset);
        return Result.success("修改成功");
    }

    @OperLog(title = "删除资产", businessType = "DELETE")
    @Operation(summary = "删除资产")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return Result.success("删除成功");
    }

    @OperLog(title = "资产领用", businessType = "UPDATE")
    @Operation(summary = "资产领用")
    @PostMapping("/{id}/assign")
    public Result<Void> assign(@PathVariable Long id, @RequestParam Long userId, @RequestParam String userName) {
        assetService.assign(id, userId, userName);
        return Result.success("领用成功");
    }

    @OperLog(title = "资产归还", businessType = "UPDATE")
    @Operation(summary = "资产归还")
    @PostMapping("/{id}/return")
    public Result<Void> returnAsset(@PathVariable Long id) {
        assetService.returnAsset(id);
        return Result.success("归还成功");
    }

    @OperLog(title = "资产报废", businessType = "UPDATE")
    @Operation(summary = "资产报废")
    @PostMapping("/{id}/scrap")
    public Result<Void> scrap(@PathVariable Long id) {
        assetService.scrap(id);
        return Result.success("报废成功");
    }

    @Operation(summary = "资产统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics() {
        return Result.success(assetService.getStatistics());
    }
}
