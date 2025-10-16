package com.yxly.service.impl;

import com.yxly.config.MinioConfig;
import com.yxly.exception.BusinessException;
import com.yxly.common.ResultCode;
import com.yxly.service.MinioService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * MinIO对象存储服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {
    
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    
    @Override
    public String uploadFile(MultipartFile file, String objectName) {
        try {
            // 检查存储桶是否存在，不存在则创建
            if (!bucketExists(minioConfig.getBucketName())) {
                createBucket(minioConfig.getBucketName());
            }
            
            // 获取文件输入流
            InputStream inputStream = file.getInputStream();
            
            // 上传文件
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            
            log.info("文件上传成功: {}", objectName);
            return getFileUrl(objectName);
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.FILE_UPLOAD_FAILED, "文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public String uploadRoomImage(MultipartFile file, Long roomId) {
        // 验证文件
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "文件不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "只支持图片文件");
        }
        
        // 验证文件大小（限制为10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "图片大小不能超过10MB");
        }
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 构建对象名称：rooms/年月日/房间ID_UUID.扩展名
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String objectName = String.format("rooms/%s/%d_%s%s", dateStr, roomId, uuid, fileExtension);
        
        return uploadFile(file, objectName);
    }
    
    @Override
    public boolean deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .object(objectName)
                    .build()
            );
            log.info("文件删除成功: {}", objectName);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean createBucket(String bucketName) {
        try {
            minioClient.makeBucket(
                MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
            log.info("存储桶创建成功: {}", bucketName);
            return true;
        } catch (Exception e) {
            log.error("存储桶创建失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String getFileUrl(String objectName) {
        return minioConfig.getUrlPrefix() + objectName;
    }
    
    /**
     * 从完整URL中提取对象名称
     * @param fileUrl 完整的文件URL
     * @return 对象名称
     */
    public String extractObjectName(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(minioConfig.getUrlPrefix())) {
            return null;
        }
        return fileUrl.substring(minioConfig.getUrlPrefix().length());
    }
}
