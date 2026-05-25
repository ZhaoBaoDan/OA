package com.smartauto.oa.document.service.impl;

import com.smartauto.oa.document.entity.DocumentSearchDoc;
import com.smartauto.oa.document.mapper.DocumentSearchRepository;
import com.smartauto.oa.document.service.IDocumentSearchService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档全文检索服务实现（Elasticsearch）
 */
@Service
public class DocumentSearchServiceImpl implements IDocumentSearchService {

    private final DocumentSearchRepository searchRepository;

    public DocumentSearchServiceImpl(DocumentSearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public void indexDocument(DocumentSearchDoc doc) {
        searchRepository.save(doc);
    }

    @Override
    public void deleteIndex(Long id) {
        searchRepository.deleteById(id);
    }

    @Override
    public List<DocumentSearchDoc> search(String keyword) {
        List<DocumentSearchDoc> results = new ArrayList<>();
        searchRepository.findByFileNameContaining(keyword).forEach(results::add);
        return results;
    }

    @Override
    public List<DocumentSearchDoc> searchByType(String fileType) {
        List<DocumentSearchDoc> results = new ArrayList<>();
        searchRepository.findByFileType(fileType).forEach(results::add);
        return results;
    }
}
