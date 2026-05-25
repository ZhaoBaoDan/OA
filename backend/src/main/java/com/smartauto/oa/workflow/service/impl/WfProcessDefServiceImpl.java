package com.smartauto.oa.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.workflow.entity.WfProcessDef;
import com.smartauto.oa.workflow.mapper.WfProcessDefMapper;
import com.smartauto.oa.workflow.service.IWfProcessDefService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 流程定义服务实现
 */
@Service
public class WfProcessDefServiceImpl implements IWfProcessDefService {

    private final WfProcessDefMapper processDefMapper;

    public WfProcessDefServiceImpl(WfProcessDefMapper processDefMapper) {
        this.processDefMapper = processDefMapper;
    }

    @Override
    public PageResult<WfProcessDef> page(WfProcessDef query, int pageNum, int pageSize) {
        Page<WfProcessDef> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WfProcessDef> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getProcessKey()), WfProcessDef::getProcessKey, query.getProcessKey())
               .like(StringUtils.hasText(query.getProcessName()), WfProcessDef::getProcessName, query.getProcessName())
               .eq(query.getStatus() != null, WfProcessDef::getStatus, query.getStatus())
               .orderByDesc(WfProcessDef::getCreateTime);
        Page<WfProcessDef> result = processDefMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public WfProcessDef getById(Long id) {
        return processDefMapper.selectById(id);
    }

    @Override
    public List<WfProcessDef> listAll() {
        LambdaQueryWrapper<WfProcessDef> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfProcessDef::getStatus, 1).orderByDesc(WfProcessDef::getCreateTime);
        return processDefMapper.selectList(wrapper);
    }

    @Override
    public void create(WfProcessDef processDef) {
        // 设置默认版本号
        if (processDef.getVersion() == null) {
            processDef.setVersion(1);
        }
        if (processDef.getStatus() == null) {
            processDef.setStatus(0);
        }
        processDefMapper.insert(processDef);
    }

    @Override
    public void update(WfProcessDef processDef) {
        WfProcessDef existing = processDefMapper.selectById(processDef.getId());
        if (existing == null) {
            throw new BusinessException("流程定义不存在");
        }
        processDefMapper.updateById(processDef);
    }

    @Override
    public void delete(List<Long> ids) {
        processDefMapper.deleteBatchIds(ids);
    }

    @Override
    public void deploy(Long id) {
        WfProcessDef processDef = processDefMapper.selectById(id);
        if (processDef == null) {
            throw new BusinessException("流程定义不存在");
        }
        processDef.setStatus(1);
        processDefMapper.updateById(processDef);
    }

    @Override
    public void suspend(Long id) {
        WfProcessDef processDef = processDefMapper.selectById(id);
        if (processDef == null) {
            throw new BusinessException("流程定义不存在");
        }
        processDef.setStatus(2);
        processDefMapper.updateById(processDef);
    }
}
