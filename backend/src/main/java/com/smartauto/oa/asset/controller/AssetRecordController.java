package com.smartauto.oa.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.asset.entity.AssetRecord;
import com.smartauto.oa.asset.mapper.AssetRecordMapper;
import com.smartauto.oa.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产流转记录控制器
 */
@Tag(name = "资产流转记录")
@RestController
@RequestMapping("/asset/record")
public class AssetRecordController {

    private final AssetRecordMapper recordMapper;

    public AssetRecordController(AssetRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Operation(summary = "资产流转记录列表")
    @GetMapping
    public Result<List<AssetRecord>> list(
            @Parameter(description = "资产ID") @RequestParam Long assetId) {
        List<AssetRecord> records = recordMapper.selectList(
                new LambdaQueryWrapper<AssetRecord>()
                        .eq(AssetRecord::getAssetId, assetId)
                        .orderByDesc(AssetRecord::getOperateTime));
        return Result.success(records);
    }
}
