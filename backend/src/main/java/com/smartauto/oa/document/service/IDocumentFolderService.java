package com.smartauto.oa.document.service;

import com.smartauto.oa.document.entity.DocumentFolder;

import java.util.List;

/**
 * 文档文件夹服务接口
 */
public interface IDocumentFolderService {

    List<DocumentFolder> tree();

    DocumentFolder getById(Long id);

    void create(DocumentFolder folder);

    void update(DocumentFolder folder);

    void delete(Long id);
}
