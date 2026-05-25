package com.smartauto.oa.document.service;

import com.smartauto.oa.document.entity.DocumentSearchDoc;

import java.util.List;

/**
 * 文档全文检索服务接口
 */
public interface IDocumentSearchService {

    /**
     * 索引文档
     */
    void indexDocument(DocumentSearchDoc doc);

    /**
     * 删除索引
     */
    void deleteIndex(Long id);

    /**
     * 全文搜索
     */
    List<DocumentSearchDoc> search(String keyword);

    /**
     * 按类型搜索
     */
    List<DocumentSearchDoc> searchByType(String fileType);
}
