package com.smartauto.oa.document.mapper;

import com.smartauto.oa.document.entity.DocumentSearchDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 文档搜索 Repository（Elasticsearch）
 */
public interface DocumentSearchRepository extends ElasticsearchRepository<DocumentSearchDoc, Long> {

    /**
     * 按文件名模糊搜索
     */
    List<DocumentSearchDoc> findByFileNameContaining(String keyword);

    /**
     * 按文件类型搜索
     */
    List<DocumentSearchDoc> findByFileType(String fileType);

    /**
     * 按上传人搜索
     */
    List<DocumentSearchDoc> findByUploadBy(Long uploadBy);
}
