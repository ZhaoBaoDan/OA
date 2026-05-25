package com.smartauto.oa.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.task.entity.BizTask;
import com.smartauto.oa.task.mapper.BizTaskMapper;
import com.smartauto.oa.task.service.IBizTaskService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 任务服务实现
 */
@Service
public class BizTaskServiceImpl implements IBizTaskService {

    private final BizTaskMapper taskMapper;

    public BizTaskServiceImpl(BizTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public PageResult<BizTask> page(BizTask query, int pageNum, int pageSize) {
        Page<BizTask> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BizTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTitle()), BizTask::getTitle, query.getTitle())
               .eq(StringUtils.hasText(query.getStatus()), BizTask::getStatus, query.getStatus())
               .eq(StringUtils.hasText(query.getPriority()), BizTask::getPriority, query.getPriority())
               .eq(query.getAssigneeId() != null, BizTask::getAssigneeId, query.getAssigneeId())
               .orderByAsc(BizTask::getSort)
               .orderByDesc(BizTask::getCreateTime);
        Page<BizTask> result = taskMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public BizTask getById(Long id) {
        return taskMapper.selectById(id);
    }

    @Override
    public List<BizTask> listByStatus(String status) {
        LambdaQueryWrapper<BizTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizTask::getStatus, status).orderByAsc(BizTask::getSort);
        return taskMapper.selectList(wrapper);
    }

    @Override
    public Map<String, List<BizTask>> board() {
        Map<String, List<BizTask>> board = new LinkedHashMap<>();
        board.put("todo", listByStatus("todo"));
        board.put("progress", listByStatus("progress"));
        board.put("done", listByStatus("done"));
        board.put("archive", listByStatus("archive"));
        return board;
    }

    @Override
    public void create(BizTask task) {
        if (task.getStatus() == null) {
            task.setStatus("todo");
        }
        if (task.getPriority() == null) {
            task.setPriority("medium");
        }
        taskMapper.insert(task);
    }

    @Override
    public void update(BizTask task) {
        BizTask existing = taskMapper.selectById(task.getId());
        if (existing == null) {
            throw new BusinessException("任务不存在");
        }
        taskMapper.updateById(task);
    }

    @Override
    public void updateStatus(Long id, String status) {
        BizTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        task.setStatus(status);
        taskMapper.updateById(task);
    }

    @Override
    public void delete(List<Long> ids) {
        taskMapper.deleteBatchIds(ids);
    }
}
