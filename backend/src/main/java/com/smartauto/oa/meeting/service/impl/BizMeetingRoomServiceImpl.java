package com.smartauto.oa.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.meeting.entity.BizMeetingRoom;
import com.smartauto.oa.meeting.mapper.BizMeetingRoomMapper;
import com.smartauto.oa.meeting.service.IBizMeetingRoomService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 会议室服务实现
 */
@Service
public class BizMeetingRoomServiceImpl implements IBizMeetingRoomService {

    private final BizMeetingRoomMapper roomMapper;

    public BizMeetingRoomServiceImpl(BizMeetingRoomMapper roomMapper) {
        this.roomMapper = roomMapper;
    }

    @Override
    public PageResult<BizMeetingRoom> page(BizMeetingRoom query, int pageNum, int pageSize) {
        Page<BizMeetingRoom> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BizMeetingRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()), BizMeetingRoom::getName, query.getName())
               .eq(query.getStatus() != null, BizMeetingRoom::getStatus, query.getStatus())
               .orderByAsc(BizMeetingRoom::getId);
        Page<BizMeetingRoom> result = roomMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public List<BizMeetingRoom> listAll() {
        LambdaQueryWrapper<BizMeetingRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BizMeetingRoom::getStatus, 1).orderByAsc(BizMeetingRoom::getId);
        return roomMapper.selectList(wrapper);
    }

    @Override
    public BizMeetingRoom getById(Long id) {
        return roomMapper.selectById(id);
    }

    @Override
    public void create(BizMeetingRoom room) {
        roomMapper.insert(room);
    }

    @Override
    public void update(BizMeetingRoom room) {
        BizMeetingRoom existing = roomMapper.selectById(room.getId());
        if (existing == null) {
            throw new BusinessException("会议室不存在");
        }
        roomMapper.updateById(room);
    }

    @Override
    public void delete(List<Long> ids) {
        roomMapper.deleteBatchIds(ids);
    }
}
