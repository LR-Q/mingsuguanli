package com.yxly.controller.admin;

import com.yxly.common.Result;
import com.yxly.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * MinIO测试控制器
 */
@RestController
@RequestMapping("/v1/admin/minio-test")
@RequiredArgsConstructor
@Slf4j
public class MinioTestController {
    
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    
    @GetMapping("/config")
    public Result<Object> getConfig() {
        return Result.success(new Object() {
            public String endpoint = minioConfig.getEndpoint();
            public String accessKey = minioConfig.getAccessKey();
            public String secretKey = "***" + minioConfig.getSecretKey().substring(Math.max(0, minioConfig.getSecretKey().length() - 4));
            public String bucketName = minioConfig.getBucketName();
        }, "配置信息");
    }
    
    @GetMapping("/test-connection")
    public Result<String> testConnection() {
        try {
            log.info("测试MinIO连接 - Endpoint: {}, AccessKey: {}", 
                minioConfig.getEndpoint(), minioConfig.getAccessKey());
            
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(minioConfig.getBucketName())
                    .build()
            );
            
            if (exists) {
                return Result.success("连接成功，存储桶存在", "连接成功");
            } else {
                return Result.success("连接成功，但存储桶不存在", "连接成功");
            }
        } catch (Exception e) {
            log.error("MinIO连接失败", e);
            return Result.error("连接失败: " + e.getMessage());
        }
    }
}
