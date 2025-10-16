package com.yxly.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * MinIO对象存储服务接口
 */
public interface MinioService {
    
    /**
     * 上传文件
     * @param file 文件
     * @param objectName 对象名称（文件路径）
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String objectName);
    
    /**
     * 上传房间图片
     * @param file 图片文件
     * @param roomId 房间ID
     * @return 图片访问URL
     */
    String uploadRoomImage(MultipartFile file, Long roomId);
    
    /**
     * 删除文件
     * @param objectName 对象名称（文件路径）
     * @return 是否删除成功
     */
    boolean deleteFile(String objectName);
    
    /**
     * 检查存储桶是否存在
     * @param bucketName 存储桶名称
     * @return 是否存在
     */
    boolean bucketExists(String bucketName);
    
    /**
     * 创建存储桶
     * @param bucketName 存储桶名称
     * @return 是否创建成功
     */
    boolean createBucket(String bucketName);
    
    /**
     * 获取文件访问URL
     * @param objectName 对象名称
     * @return 访问URL
     */
    String getFileUrl(String objectName);
}
