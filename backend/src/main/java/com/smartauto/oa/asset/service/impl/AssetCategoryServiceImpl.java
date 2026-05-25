package com.smartauto.oa.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.asset.entity.AssetCategory;
import com.smartauto.oa.asset.mapper.AssetCategoryMapper;
import com.smartauto.oa.asset.service.AssetCategoryService;
import com.smartauto.oa.common.BusinessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资产分类服务实现
 */
@Service
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private final AssetCategoryMapper categoryMapper;

    public AssetCategoryServiceImpl(AssetCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<AssetCategory> tree() {
        List<AssetCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<AssetCategory>()
                        .eq(AssetCategory::getDelFlag, 0)
                        .orderByAsc(AssetCategory::getId));
        return buildTree(all, 0L);
    }

    @Override
    public AssetCategory getById(Long id) {
        AssetCategory category = categoryMapper.selectById(id);
        if (category == null) throw new BusinessException("分类不存在");
        return category;
    }

    @Override
    public void create(AssetCategory category) {
        categoryMapper.insert(category);
    }

    @Override
    public void update(AssetCategory category) {
        if (categoryMapper.selectById(category.getId()) == null) {
            throw new BusinessException("分类不存在");
        }
        categoryMapper.updateById(category);
    }

    @Override
    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }

    private List<AssetCategory> buildTree(List<AssetCategory> all, Long parentId) {
        Map<Long, List<AssetCategory>> groupMap = all.stream()
                .collect(Collectors.groupingBy(c -> c.getParentId() != null ? c.getParentId() : 0L));
        return buildChildren(groupMap, parentId);
    }

    private List<AssetCategory> buildChildren(Map<Long, List<AssetCategory>> groupMap, Long parentId) {
        List<AssetCategory> children = groupMap.getOrDefault(parentId, new ArrayList<>());
        for (AssetCategory child : children) {
            child.setChildren(buildChildren(groupMap, child.getId()));
        }
        return children;
    }
}
