package com.yxly.controller.admin;

import com.yxly.common.Result;
import com.yxly.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/v1/admin/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "文件管理", description = "文件上传下载相关接口")
public class FileController {
    
    private final MinioService minioService;
    
    /**
     * 上传房间图片
     */
    @PostMapping("/upload/room-image")
    @Operation(summary = "上传房间图片", description = "上传房间图片到MinIO存储")
    public Result<String> uploadRoomImage(
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "房间ID", required = true)
            @RequestParam("roomId") Long roomId) {
        
        log.info("开始上传房间图片，房间ID: {}, 文件名: {}", roomId, file.getOriginalFilename());
        
        String imageUrl = minioService.uploadRoomImage(file, roomId);
        
        log.info("房间图片上传成功，URL: {}", imageUrl);
        return Result.success(imageUrl, "图片上传成功");
    }
    
    /**
     * 测试MinIO连接
     */
    @GetMapping("/test-minio")
    @Operation(summary = "测试MinIO连接", description = "测试MinIO服务器连接状态")
    public Result<String> testMinio() {
        try {
            boolean exists = minioService.bucketExists("txt");
            if (exists) {
                return Result.success("MinIO连接正常，存储桶txt存在", "连接成功");
            } else {
                // 尝试创建存储桶
                boolean created = minioService.createBucket("txt");
                if (created) {
                    return Result.success("MinIO连接正常，已创建存储桶txt", "连接成功");
                } else {
                    return Result.error("MinIO连接失败，无法创建存储桶");
                }
            }
        } catch (Exception e) {
            log.error("MinIO连接测试失败", e);
            return Result.error("MinIO连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 通用文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "通用文件上传", description = "上传文件到MinIO存储")
    public Result<String> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件路径（可选）")
            @RequestParam(value = "path", required = false) String path) {
        
        log.info("开始上传文件，文件名: {}, 路径: {}", file.getOriginalFilename(), path);
        
        // 如果没有指定路径，使用默认路径
        if (path == null || path.trim().isEmpty()) {
            path = "uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        }
        
        String fileUrl = minioService.uploadFile(file, path);
        
        log.info("文件上传成功，URL: {}", fileUrl);
        return Result.success(fileUrl, "文件上传成功");
    }
    
    /**
     * 上传支付凭证
     */
    @PostMapping("/upload/payment-proof")
    @Operation(summary = "上传支付凭证", description = "上传用户充值的支付凭证图片")
    public Result<String> uploadPaymentProof(
            @Parameter(description = "支付凭证图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("上传支付凭证: fileName={}, size={}", file.getOriginalFilename(), file.getSize());
        
        try {
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }
            
            // 验证文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("文件大小不能超过10MB");
            }
            
            String imageUrl = minioService.uploadFile(file, "payment-proof/" + System.currentTimeMillis() + "_" + file.getOriginalFilename());
            return Result.success(imageUrl, "支付凭证上传成功");
        } catch (Exception e) {
            log.error("支付凭证上传失败: fileName={}", file.getOriginalFilename(), e);
            return Result.error("支付凭证上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除文件", description = "从MinIO存储中删除文件")
    public Result<Void> deleteFile(
            @Parameter(description = "文件URL", required = true)
            @RequestParam("fileUrl") String fileUrl) {
        
        log.info("开始删除文件，URL: {}", fileUrl);
        
        // 从URL中提取对象名称
        String objectName = ((com.yxly.service.impl.MinioServiceImpl) minioService).extractObjectName(fileUrl);
        if (objectName == null) {
            return Result.error("无效的文件URL");
        }
        
        boolean success = minioService.deleteFile(objectName);
        if (success) {
            log.info("文件删除成功，对象名称: {}", objectName);
            return Result.success(null, "文件删除成功");
        } else {
            log.error("文件删除失败，对象名称: {}", objectName);
            return Result.error("文件删除失败");
        }
    }
}
