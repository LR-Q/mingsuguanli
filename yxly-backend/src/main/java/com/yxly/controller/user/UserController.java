package com.yxly.controller.user;

import com.yxly.common.Result;
import com.yxly.entity.SysUser;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.MinioService;
import com.yxly.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户信息", description = "用户信息相关接口")
public class UserController {
    
    private final SecurityUtils securityUtils;
    private final MinioService minioService;
    private final SysUserMapper sysUserMapper;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<Map<String, Object>> getCurrentUserInfo() {
        
        SysUser user = securityUtils.getCurrentUser();
        if (user == null) {
            log.error("用户未登录，无法获取用户信息");
            return Result.error("请先登录后再查询用户信息");
        }
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("email", user.getEmail());
        userInfo.put("phone", user.getPhone());
        userInfo.put("avatar", user.getAvatar());
        userInfo.put("balance", user.getBalance() != null ? user.getBalance() : new BigDecimal("0.00"));
        
        return Result.success(userInfo, "获取用户信息成功");
    }
    
    /**
     * 获取用户余额
     */
    @GetMapping("/balance")
    @Operation(summary = "获取用户余额")
    public Result<BigDecimal> getUserBalance() {
        
        SysUser user = securityUtils.getCurrentUser();
        if (user == null) {
            log.error("用户未登录，无法获取余额");
            return Result.error("请先登录后再查询余额");
        }
        
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : new BigDecimal("0.00");
        return Result.success(balance, "获取余额成功");
    }
    
    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像")
    public Result<Map<String, String>> uploadAvatar(
            @Parameter(description = "头像图片文件", required = true)
            @RequestParam("file") MultipartFile file) {
        
        log.info("接收到头像上传请求: filename={}, size={}", file.getOriginalFilename(), file.getSize());
        
        try {
            // 获取当前用户
            SysUser user = securityUtils.getCurrentUser();
            if (user == null) {
                return Result.error(401, "用户未登录");
            }
            
            // 验证文件
            if (file.isEmpty()) {
                return Result.error(400, "请选择文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error(400, "只能上传图片文件");
            }
            
            // 验证文件大小（2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.error(400, "图片大小不能超过2MB");
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 构建MinIO对象名称：avatars/用户ID/年月日/UUID.扩展名
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String objectName = String.format("avatars/%s/%s/%s%s", user.getId(), dateStr, uuid, extension);
            
            // 使用MinIO服务上传文件
            String fileUrl = minioService.uploadFile(file, objectName);
            
            // 更新用户头像URL
            user.setAvatar(fileUrl);
            user.setUpdateTime(LocalDateTime.now());
            sysUserMapper.updateById(user);
            
            log.info("头像上传成功: userId={}, url={}", user.getId(), fileUrl);
            
            // 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("filename", uuid + extension);
            result.put("originalFilename", originalFilename);
            
            return Result.success(result, "头像上传成功");
            
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error(500, "上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    @Operation(summary = "更新用户信息")
    public Result<Map<String, Object>> updateProfile(
            @Parameter(description = "用户信息", required = true)
            @RequestBody Map<String, Object> profileData) {
        
        try {
            // 获取当前用户
            SysUser user = securityUtils.getCurrentUser();
            if (user == null) {
                return Result.error(401, "用户未登录");
            }
            
            log.info("更新用户信息: userId={}, data={}", user.getId(), profileData);
            
            // 更新允许修改的字段
            if (profileData.containsKey("realName")) {
                user.setRealName((String) profileData.get("realName"));
            }
            if (profileData.containsKey("email")) {
                user.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("phone")) {
                user.setPhone((String) profileData.get("phone"));
            }
            if (profileData.containsKey("avatar")) {
                user.setAvatar((String) profileData.get("avatar"));
            }
            
            user.setUpdateTime(LocalDateTime.now());
            sysUserMapper.updateById(user);
            
            // 返回更新后的用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("realName", user.getRealName());
            userInfo.put("email", user.getEmail());
            userInfo.put("phone", user.getPhone());
            userInfo.put("avatar", user.getAvatar());
            userInfo.put("balance", user.getBalance() != null ? user.getBalance() : new BigDecimal("0.00"));
            
            log.info("用户信息更新成功: userId={}", user.getId());
            return Result.success(userInfo, "用户信息更新成功");
            
        } catch (Exception e) {
            log.error("用户信息更新失败", e);
            return Result.error(500, "更新失败: " + e.getMessage());
        }
    }
}
