package com.yxly.controller.upload;

import com.yxly.common.Result;
import com.yxly.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器（使用MinIO对象存储）
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/v1/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "图片、文件上传相关接口")
public class UploadController {
    
    private final MinioService minioService;
    
    @Operation(summary = "上传图片", description = "上传图片文件到MinIO（支持jpg、png、jpeg等格式）")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("接收到图片上传请求: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error(400, "请选择文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error(400, "只能上传图片文件");
            }
            
            // 验证文件大小（5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                return Result.error(400, "图片大小不能超过5MB");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 构建MinIO对象名称：idcards/年月日/UUID.扩展名
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String objectName = String.format("idcards/%s/%s%s", dateStr, uuid, extension);
            
            // 使用MinIO服务上传文件
            String fileUrl = minioService.uploadFile(file, objectName);
            
            log.info("图片上传成功: {}", fileUrl);
            
            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", uuid + extension);
            result.put("originalFilename", originalFilename);
            result.put("objectName", objectName);
            
            return Result.success(result, "上传成功");
            
        } catch (Exception e) {
            log.error("图片上传失败", e);
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "上传身份证图片", description = "上传身份证图片到MinIO")
    @PostMapping("/idcard")
    public Result<Map<String, String>> uploadIdCard(@RequestParam("file") MultipartFile file) {
        return uploadImage(file);
    }
}
