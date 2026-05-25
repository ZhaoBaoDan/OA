package com.smartauto.oa.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.asset.entity.Asset;
import com.smartauto.oa.asset.entity.AssetRecord;
import com.smartauto.oa.asset.mapper.AssetMapper;
import com.smartauto.oa.asset.mapper.AssetRecordMapper;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产管理服务实现
 */
@Service
public class AssetServiceImpl implements AssetService {

    private final AssetMapper assetMapper;
    private final AssetRecordMapper recordMapper;

    public AssetServiceImpl(AssetMapper assetMapper, AssetRecordMapper recordMapper) {
        this.assetMapper = assetMapper;
        this.recordMapper = recordMapper;
    }

    @Override
    public PageResult<Asset> page(Asset query, int pageNum, int pageSize) {
        Page<Asset> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getAssetCode()), Asset::getAssetCode, query.getAssetCode())
               .like(StringUtils.hasText(query.getName()), Asset::getName, query.getName())
               .eq(query.getCategoryId() != null, Asset::getCategoryId, query.getCategoryId())
               .eq(query.getStatus() != null, Asset::getStatus, query.getStatus())
               .eq(StringUtils.hasText(query.getLocation()), Asset::getLocation, query.getLocation())
               .orderByDesc(Asset::getCreateTime);
        Page<Asset> result = assetMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public Asset getById(Long id) {
        Asset asset = assetMapper.selectById(id);
        if (asset == null) throw new BusinessException("资产不存在");
        return asset;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Asset asset) {
        // 自动生成资产编码: AST + 年月日 + 4位序号
        String prefix = "AST" + LocalDate.now().toString().replace("-", "");
        Long count = assetMapper.selectCount(new LambdaQueryWrapper<Asset>().likeRight(Asset::getAssetCode, prefix));
        asset.setAssetCode(prefix + String.format("%04d", count + 1));
        asset.setCurrentValue(asset.getPurchasePrice()); // 初始净值=原值
        assetMapper.insert(asset);
        // 记录入库操作
        saveRecord(asset.getId(), "IN", null, null, null, null, null, "资产入库");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Asset asset) {
        if (assetMapper.selectById(asset.getId()) == null) throw new BusinessException("资产不存在");
        assetMapper.updateById(asset);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Asset asset = getById(id);
        if (asset.getStatus() == 1) throw new BusinessException("在用资产不可删除");
        assetMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long assetId, Long userId, String userName) {
        Asset asset = getById(assetId);
        if (asset.getStatus() != 0) throw new BusinessException("只有闲置资产才能领用");
        asset.setUserId(userId);
        asset.setUserName(userName);
        asset.setStatus(1); // 在用
        assetMapper.updateById(asset);
        saveRecord(assetId, "ASSIGN", userId, userName, null, userId, userName, "资产领用");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnAsset(Long assetId) {
        Asset asset = getById(assetId);
        if (asset.getStatus() != 1) throw new BusinessException("只有在用资产才能归还");
        Long fromUserId = asset.getUserId();
        String fromUserName = asset.getUserName();
        asset.setUserId(null);
        asset.setUserName(null);
        asset.setStatus(0); // 闲置
        assetMapper.updateById(asset);
        saveRecord(assetId, "RETURN", fromUserId, fromUserName, fromUserId, null, null, "资产归还");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scrap(Long assetId) {
        Asset asset = getById(assetId);
        if (asset.getStatus() == 3) throw new BusinessException("资产已报废");
        asset.setStatus(3); // 报废
        asset.setCurrentValue(java.math.BigDecimal.ZERO);
        assetMapper.updateById(asset);
        saveRecord(assetId, "SCRAP", asset.getUserId(), asset.getUserName(), asset.getUserId(), null, null, "资产报废");
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", assetMapper.selectCount(null));
        stats.put("idle", assetMapper.selectCount(new LambdaQueryWrapper<Asset>().eq(Asset::getStatus, 0)));
        stats.put("inUse", assetMapper.selectCount(new LambdaQueryWrapper<Asset>().eq(Asset::getStatus, 1)));
        stats.put("repair", assetMapper.selectCount(new LambdaQueryWrapper<Asset>().eq(Asset::getStatus, 2)));
        stats.put("scrapped", assetMapper.selectCount(new LambdaQueryWrapper<Asset>().eq(Asset::getStatus, 3)));
        return stats;
    }

    private void saveRecord(Long assetId, String type, Long operatorId, String operatorName, Long fromUserId, Long toUserId, String toUserName, String remark) {
        AssetRecord record = new AssetRecord();
        record.setAssetId(assetId);
        record.setType(type);
        record.setOperatorId(operatorId);
        record.setOperatorName(operatorName);
        record.setFromUserId(fromUserId);
        record.setToUserId(toUserId);
        record.setRemark(remark);
        recordMapper.insert(record);
    }
}
