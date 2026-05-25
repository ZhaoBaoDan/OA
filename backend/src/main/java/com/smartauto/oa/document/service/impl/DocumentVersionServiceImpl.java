package com.smartauto.oa.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartauto.oa.common.BusinessException;
import com.smartauto.oa.common.MinioStorageService;
import com.smartauto.oa.document.entity.BizDocument;
import com.smartauto.oa.document.entity.DocumentVersion;
import com.smartauto.oa.document.mapper.BizDocumentMapper;
import com.smartauto.oa.document.mapper.DocumentVersionMapper;
import com.smartauto.oa.document.service.IDocumentVersionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档版本服务实现（MinIO 存储）
 */
@Service
public class DocumentVersionServiceImpl implements IDocumentVersionService {

    private final DocumentVersionMapper versionMapper;
    private final BizDocumentMapper documentMapper;
    private final MinioStorageService storageService;

    public DocumentVersionServiceImpl(DocumentVersionMapper versionMapper,
                                      BizDocumentMapper documentMapper,
                                      MinioStorageService storageService) {
        this.versionMapper = versionMapper;
        this.documentMapper = documentMapper;
        this.storageService = storageService;
    }

    @Override
    public List<DocumentVersion> listByDocumentId(Long documentId) {
        return versionMapper.selectList(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .eq(DocumentVersion::getDelFlag, 0)
                        .orderByDesc(DocumentVersion::getVersion));
    }

    @Override
    public DocumentVersion getById(Long id) {
        DocumentVersion version = versionMapper.selectById(id);
        if (version == null) throw new BusinessException("版本不存在");
        return version;
    }

    @Override
    public DocumentVersion getLatestVersion(Long documentId) {
        return versionMapper.selectOne(
                new LambdaQueryWrapper<DocumentVersion>()
                        .eq(DocumentVersion::getDocumentId, documentId)
                        .eq(DocumentVersion::getDelFlag, 0)
                        .orderByDesc(DocumentVersion::getVersion)
                        .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createVersion(Long documentId, MultipartFile file, Long userId, String userName, String remark) {
        BizDocument doc = documentMapper.selectById(documentId);
        if (doc == null) throw new BusinessException("文档不存在");

        // 获取当前最大版本号
        DocumentVersion latest = getLatestVersion(documentId);
        int newVersion = (latest != null) ? latest.getVersion() + 1 : 1;

        // 上传到 MinIO
        String objectName = storageService.upload(file, "documents/versions/" + documentId);
        String fileUrl = storageService.getFileUrl(objectName);

        // 保存版本记录
        DocumentVersion version = new DocumentVersion();
        version.setDocumentId(documentId);
        version.setVersion(newVersion);
        version.setFilePath(objectName); // MinIO 对象名
        version.setFileSize(file.getSize());
        version.setUploaderId(userId);
        version.setUploaderName(userName);
        version.setRemark(remark);
        version.setDelFlag(0);
        versionMapper.insert(version);

        // 更新文档的文件URL和大小为最新版本
        doc.setFileUrl(fileUrl);
        doc.setFileSize(file.getSize());
        documentMapper.updateById(doc);
    }
}
