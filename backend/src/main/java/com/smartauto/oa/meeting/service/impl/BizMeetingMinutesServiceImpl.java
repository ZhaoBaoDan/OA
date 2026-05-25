package com.smartauto.oa.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.meeting.entity.BizMeetingMinutes;
import com.smartauto.oa.meeting.mapper.BizMeetingMinutesMapper;
import com.smartauto.oa.meeting.service.IBizMeetingMinutesService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 会议纪要服务实现
 */
@Service
public class BizMeetingMinutesServiceImpl implements IBizMeetingMinutesService {

    private final BizMeetingMinutesMapper minutesMapper;

    public BizMeetingMinutesServiceImpl(BizMeetingMinutesMapper minutesMapper) {
        this.minutesMapper = minutesMapper;
    }

    @Override
    public PageResult<BizMeetingMinutes> page(BizMeetingMinutes query, int pageNum, int pageSize) {
        Page<BizMeetingMinutes> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BizMeetingMinutes> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTitle()), BizMeetingMinutes::getTitle, query.getTitle())
               .eq(query.getRoomId() != null, BizMeetingMinutes::getRoomId, query.getRoomId())
               .eq(query.getOrganizerId() != null, BizMeetingMinutes::getOrganizerId, query.getOrganizerId())
               .eq(BizMeetingMinutes::getDelFlag, 0)
               .orderByDesc(BizMeetingMinutes::getMeetingDate)
               .orderByDesc(BizMeetingMinutes::getCreateTime);
        Page<BizMeetingMinutes> result = minutesMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public BizMeetingMinutes getById(Long id) {
        BizMeetingMinutes minutes = minutesMapper.selectById(id);
        if (minutes == null) throw new BusinessException("会议纪要不存在");
        return minutes;
    }

    @Override
    public void create(BizMeetingMinutes minutes) {
        minutes.setDelFlag(0);
        minutesMapper.insert(minutes);
    }

    @Override
    public void update(BizMeetingMinutes minutes) {
        if (minutesMapper.selectById(minutes.getId()) == null) {
            throw new BusinessException("会议纪要不存在");
        }
        minutesMapper.updateById(minutes);
    }

    @Override
    public void delete(Long id) {
        minutesMapper.deleteById(id);
    }
}
