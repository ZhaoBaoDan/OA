package com.smartauto.oa.workflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.workflow.entity.WfApprovalRecord;
import com.smartauto.oa.workflow.entity.WfProcessInst;
import com.smartauto.oa.workflow.mapper.WfApprovalRecordMapper;
import com.smartauto.oa.workflow.mapper.WfProcessInstMapper;
import com.smartauto.oa.workflow.service.IWfProcessInstService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程实例服务实现
 */
@Service
public class WfProcessInstServiceImpl implements IWfProcessInstService {

    private final WfProcessInstMapper processInstMapper;
    private final WfApprovalRecordMapper approvalRecordMapper;

    public WfProcessInstServiceImpl(WfProcessInstMapper processInstMapper,
                                     WfApprovalRecordMapper approvalRecordMapper) {
        this.processInstMapper = processInstMapper;
        this.approvalRecordMapper = approvalRecordMapper;
    }

    @Override
    public PageResult<WfProcessInst> page(WfProcessInst query, int pageNum, int pageSize) {
        Page<WfProcessInst> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<WfProcessInst> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getProcessName()), WfProcessInst::getProcessName, query.getProcessName())
               .like(StringUtils.hasText(query.getStartUserName()), WfProcessInst::getStartUserName, query.getStartUserName())
               .eq(StringUtils.hasText(query.getStatus()), WfProcessInst::getStatus, query.getStatus())
               .orderByDesc(WfProcessInst::getStartTime);
        Page<WfProcessInst> result = processInstMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public WfProcessInst getById(Long id) {
        return processInstMapper.selectById(id);
    }

    @Override
    public List<WfApprovalRecord> getApprovalRecords(Long processInstId) {
        LambdaQueryWrapper<WfApprovalRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WfApprovalRecord::getProcessInstId, processInstId).orderByAsc(WfApprovalRecord::getCreateTime);
        return approvalRecordMapper.selectList(wrapper);
    }

    @Override
    public void terminate(Long id) {
        WfProcessInst inst = processInstMapper.selectById(id);
        if (inst == null) {
            throw new BusinessException("流程实例不存在");
        }
        if (!"running".equals(inst.getStatus())) {
            throw new BusinessException("只有进行中的流程才能终止");
        }
        inst.setStatus("terminated");
        inst.setEndTime(LocalDateTime.now());
        if (inst.getStartTime() != null) {
            inst.setDuration(Duration.between(inst.getStartTime(), LocalDateTime.now()).toMillis());
        }
        processInstMapper.updateById(inst);
    }

    @Override
    public void delete(List<Long> ids) {
        processInstMapper.deleteBatchIds(ids);
    }
}
