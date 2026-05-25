package com.smartauto.oa.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.system.entity.SysPost;
import com.smartauto.oa.system.mapper.SysPostMapper;
import com.smartauto.oa.system.service.ISysPostService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 岗位管理服务实现
 */
@Service
public class SysPostServiceImpl implements ISysPostService {

    private final SysPostMapper postMapper;

    public SysPostServiceImpl(SysPostMapper postMapper) {
        this.postMapper = postMapper;
    }

    @Override
    public PageResult<SysPost> page(SysPost query, int pageNum, int pageSize) {
        Page<SysPost> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getPostCode()), SysPost::getPostCode, query.getPostCode())
               .like(StringUtils.hasText(query.getPostName()), SysPost::getPostName, query.getPostName())
               .eq(query.getStatus() != null, SysPost::getStatus, query.getStatus())
               .orderByAsc(SysPost::getSort);
        Page<SysPost> result = postMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public void create(SysPost post) {
        // 校验岗位编码唯一
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getPostCode, post.getPostCode());
        Long count = postMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("岗位编码'" + post.getPostCode() + "'已存在");
        }
        postMapper.insert(post);
    }

    @Override
    public void update(SysPost post) {
        SysPost existing = postMapper.selectById(post.getId());
        if (existing == null) {
            throw new BusinessException("岗位不存在");
        }
        // 校验岗位编码唯一（排除自身）
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPost::getPostCode, post.getPostCode()).ne(SysPost::getId, post.getId());
        Long count = postMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("岗位编码'" + post.getPostCode() + "'已存在");
        }
        postMapper.updateById(post);
    }

    @Override
    public void delete(List<Long> ids) {
        postMapper.deleteBatchIds(ids);
    }
}
