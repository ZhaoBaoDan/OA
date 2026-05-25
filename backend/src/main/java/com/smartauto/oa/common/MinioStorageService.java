package com.smartauto.oa.common;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 文件存储服务
 */
@Service
public class MinioStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:smartauto-oa}")
    private String bucketName;

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    public MinioStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * 确保 Bucket 存在
     */
    public void ensureBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("MinIO bucket 检查失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件
     * @param file 文件
     * @param directory 目录前缀（如 "documents", "avatars"）
     * @return 对象名称（含目录前缀）
     */
    public String upload(MultipartFile file, String directory) {
        try {
            ensureBucket();
            String originalName = file.getOriginalFilename();
            String suffix = "";
            if (originalName != null && originalName.contains(".")) {
                suffix = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }
            String objectName = directory + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到 MinIO 失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件（InputStream）
     */
    public String upload(InputStream inputStream, String objectName, String contentType) {
        try {
            ensureBucket();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, -1, 10485760) // 10MB part size
                    .contentType(contentType)
                    .build());
            return objectName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传到 MinIO 失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件下载/预览 URL（临时链接，7天有效）
     */
    public String getFileUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .method(Method.GET)
                    .expiry(7, TimeUnit.DAYS)
                    .build());
        } catch (Exception e) {
            // Fallback: 拼接公开 URL
            return endpoint + "/" + bucketName + "/" + objectName;
        }
    }

    /**
     * 获取文件输入流
     */
    public InputStream getFile(String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("从 MinIO 获取文件失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    public void delete(String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("从 MinIO 删除文件失败: " + e.getMessage());
        }
    }
}
