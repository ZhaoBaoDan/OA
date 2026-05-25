package com.smartauto.oa.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.document.entity.DocumentFolder;
import com.smartauto.oa.document.mapper.DocumentFolderMapper;
import com.smartauto.oa.document.service.IDocumentFolderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文档文件夹服务实现
 */
@Service
public class DocumentFolderServiceImpl implements IDocumentFolderService {

    private final DocumentFolderMapper folderMapper;

    public DocumentFolderServiceImpl(DocumentFolderMapper folderMapper) {
        this.folderMapper = folderMapper;
    }

    @Override
    public List<DocumentFolder> tree() {
        List<DocumentFolder> all = folderMapper.selectList(
                new LambdaQueryWrapper<DocumentFolder>()
                        .eq(DocumentFolder::getDelFlag, 0)
                        .orderByAsc(DocumentFolder::getId));
        return buildTree(all, 0L);
    }

    @Override
    public DocumentFolder getById(Long id) {
        DocumentFolder folder = folderMapper.selectById(id);
        if (folder == null) throw new BusinessException("文件夹不存在");
        return folder;
    }

    @Override
    public void create(DocumentFolder folder) {
        // 构建路径
        if (folder.getParentId() == null || folder.getParentId() == 0) {
            folder.setParentId(0L);
            folder.setPath("/");
        } else {
            DocumentFolder parent = getById(folder.getParentId());
            folder.setPath(parent.getPath() + parent.getId() + "/");
        }
        folder.setDelFlag(0);
        folderMapper.insert(folder);
    }

    @Override
    public void update(DocumentFolder folder) {
        if (folderMapper.selectById(folder.getId()) == null) {
            throw new BusinessException("文件夹不存在");
        }
        folderMapper.updateById(folder);
    }

    @Override
    public void delete(Long id) {
        folderMapper.deleteById(id);
    }

    private List<DocumentFolder> buildTree(List<DocumentFolder> all, Long parentId) {
        Map<Long, List<DocumentFolder>> groupMap = all.stream()
                .collect(Collectors.groupingBy(f -> f.getParentId() != null ? f.getParentId() : 0L));
        return buildChildren(groupMap, parentId);
    }

    private List<DocumentFolder> buildChildren(Map<Long, List<DocumentFolder>> groupMap, Long parentId) {
        List<DocumentFolder> children = groupMap.getOrDefault(parentId, new ArrayList<>());
        for (DocumentFolder child : children) {
            child.setChildren(buildChildren(groupMap, child.getId()));
        }
        return children;
    }
}
