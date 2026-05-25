package com.smartauto.oa.asset.service;

import com.smartauto.oa.asset.entity.Asset;
import com.smartauto.oa.common.PageResult;

import java.util.Map;

/**
 * 资产管理服务接口
 */
public interface AssetService {

    PageResult<Asset> page(Asset query, int pageNum, int pageSize);

    Asset getById(Long id);

    void create(Asset asset);

    void update(Asset asset);

    void delete(Long id);

    void assign(Long assetId, Long userId, String userName);

    void returnAsset(Long assetId);

    void scrap(Long assetId);

    Map<String, Object> getStatistics();
}
