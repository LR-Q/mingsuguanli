package com.yxly.controller.user;

import com.yxly.common.Result;
import com.yxly.entity.SysUser;
import com.yxly.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    public Result<Map<String, Object>> getCurrentUserInfo() {
        
        SysUser user = securityUtils.getCurrentUser();
        if (user == null) {
            // 临时使用默认用户信息（调试用）
            Map<String, Object> defaultUserInfo = new HashMap<>();
            defaultUserInfo.put("id", 1L);
            defaultUserInfo.put("username", "admin");
            defaultUserInfo.put("realName", "Administrator");
            defaultUserInfo.put("email", "admin@example.com");
            defaultUserInfo.put("balance", new BigDecimal("1288.50"));
            log.warn("用户未登录，使用默认用户信息");
            return Result.success(defaultUserInfo, "获取用户信息成功");
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
            // 临时使用默认余额（调试用）
            log.warn("用户未登录，使用默认余额");
            return Result.success(new BigDecimal("1288.50"), "获取余额成功");
        }
        
        BigDecimal balance = user.getBalance() != null ? user.getBalance() : new BigDecimal("0.00");
        return Result.success(balance, "获取余额成功");
    }
}
