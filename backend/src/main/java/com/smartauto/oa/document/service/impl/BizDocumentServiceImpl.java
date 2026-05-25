package com.smartauto.oa.document.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartauto.oa.common.MinioStorageService;
import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.document.entity.BizDocument;
import com.smartauto.oa.document.mapper.BizDocumentMapper;
import com.smartauto.oa.document.service.IBizDocumentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档服务实现（MinIO 存储）
 */
@Service
public class BizDocumentServiceImpl implements IBizDocumentService {

    private final BizDocumentMapper documentMapper;
    private final MinioStorageService storageService;

    public BizDocumentServiceImpl(BizDocumentMapper documentMapper, MinioStorageService storageService) {
        this.documentMapper = documentMapper;
        this.storageService = storageService;
    }

    @Override
    public PageResult<BizDocument> page(BizDocument query, int pageNum, int pageSize) {
        Page<BizDocument> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BizDocument> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getFileName()), BizDocument::getFileName, query.getFileName())
               .eq(StringUtils.hasText(query.getFileType()), BizDocument::getFileType, query.getFileType())
               .eq(query.getFolderId() != null, BizDocument::getFolderId, query.getFolderId())
               .orderByDesc(BizDocument::getCreateTime);
        Page<BizDocument> result = documentMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords(), pageNum, pageSize);
    }

    @Override
    public BizDocument getById(Long id) {
        return documentMapper.selectById(id);
    }

    @Override
    public void upload(MultipartFile file, Long folderId, Long userId, String userName) {
        String originalName = file.getOriginalFilename();
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        String fileType = resolveFileType(suffix);

        // 上传到 MinIO
        String objectName = storageService.upload(file, "documents");
        String fileUrl = storageService.getFileUrl(objectName);

        // 保存记录
        BizDocument doc = new BizDocument();
        doc.setFileName(originalName);
        doc.setFileType(fileType);
        doc.setFileSize(file.getSize());
        doc.setFolderId(folderId);
        doc.setFileUrl(fileUrl);
        doc.setStoragePath(objectName); // 存储 MinIO 对象名
        doc.setUploadBy(userId);
        doc.setUploadName(userName);
        documentMapper.insert(doc);
    }

    @Override
    public void delete(List<Long> ids) {
        // 先删除 MinIO 中的文件
        for (Long id : ids) {
            BizDocument doc = documentMapper.selectById(id);
            if (doc != null && StringUtils.hasText(doc.getStoragePath())) {
                try {
                    storageService.delete(doc.getStoragePath());
                } catch (Exception ignored) {
                    // 文件删除失败不阻断流程
                }
            }
        }
        documentMapper.deleteBatchIds(ids);
    }

    private String resolveFileType(String suffix) {
        if (suffix.isEmpty()) return "other";
        if (suffix.matches("\\.(jpg|jpeg|png|gif|bmp|webp|svg)$")) return "image";
        if (suffix.matches("\\.(doc|docx|txt|md|rtf)$")) return "document";
        if (suffix.matches("\\.(xls|xlsx|csv)$")) return "spreadsheet";
        if (suffix.equals(".pdf")) return "pdf";
        return "other";
    }
}
