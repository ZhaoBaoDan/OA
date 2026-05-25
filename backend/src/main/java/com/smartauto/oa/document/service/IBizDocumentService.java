package com.smartauto.oa.document.service;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.document.entity.BizDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档服务接口
 */
public interface IBizDocumentService {

    PageResult<BizDocument> page(BizDocument query, int pageNum, int pageSize);

    BizDocument getById(Long id);

    void upload(MultipartFile file, Long folderId, Long userId, String userName);

    void delete(List<Long> ids);
}
