package com.smartauto.oa.document.service;

import com.smartauto.oa.document.entity.DocumentVersion;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档版本服务接口
 */
public interface IDocumentVersionService {

    List<DocumentVersion> listByDocumentId(Long documentId);

    DocumentVersion getById(Long id);

    DocumentVersion getLatestVersion(Long documentId);

    void createVersion(Long documentId, MultipartFile file, Long userId, String userName, String remark);
}
