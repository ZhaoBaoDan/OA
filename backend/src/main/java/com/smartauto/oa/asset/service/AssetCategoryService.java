package com.smartauto.oa.asset.service;

import com.smartauto.oa.asset.entity.AssetCategory;

import java.util.List;

/**
 * 资产分类服务接口
 */
public interface AssetCategoryService {

    List<AssetCategory> tree();

    AssetCategory getById(Long id);

    void create(AssetCategory category);

    void update(AssetCategory category);

    void delete(Long id);
}
