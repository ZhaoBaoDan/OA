package com.smartauto.oa.document.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 文档搜索索引（Elasticsearch）
 */
@Data
@Document(indexName = "smartauto_documents")
public class DocumentSearchDoc {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String fileName;

    @Field(type = FieldType.Keyword)
    private String fileType;

    @Field(type = FieldType.Keyword)
    private Long folderId;

    @Field(type = FieldType.Long)
    private Long uploadBy;

    @Field(type = FieldType.Keyword)
    private String uploadName;

    @Field(type = FieldType.Long)
    private Long fileSize;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createTime;
}
