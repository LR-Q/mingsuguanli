package com.yxly.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.request.RechargeApplyRequest;
import com.yxly.dto.request.WithdrawApplyRequest;
import com.yxly.dto.response.RechargeRecordResponse;
import com.yxly.service.RechargeService;
import com.yxly.service.WithdrawService;
import com.yxly.util.SecurityUtils;
import com.yxly.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户充值控制器
 */
@RestController
@RequestMapping("/v1/user/recharge")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户充值", description = "用户充值申请相关接口")
public class UserRechargeController {
    
    private final RechargeService rechargeService;
    private final WithdrawService withdrawService;
    private final SecurityUtils securityUtils;
    private final SysUserMapper sysUserMapper;
    
    /**
     * 申请充值
     */
    @PostMapping("/apply")
    @Operation(summary = "申请充值")
    public Result<Void> applyRecharge(@Valid @RequestBody RechargeApplyRequest request) {
        
        // 获取当前用户ID
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            // 临时使用默认用户ID（调试用）
            userId = 1L;
            log.warn("用户未登录，使用默认用户ID: {}", userId);
        }
        
        rechargeService.applyRecharge(userId, request);
        return Result.success(null, "充值申请提交成功，请等待管理员审核");
    }
    
    /**
     * 查询用户充值记录
     */
    @GetMapping("/records")
    @Operation(summary = "查询用户充值记录")
    public Result<PageResult<RechargeRecordResponse>> getUserRechargeRecords(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {
        
        // 获取当前用户ID
        Long userId = securityUtils.getCurrentUserId();
        if (userId == null) {
            // 临时使用默认用户ID（调试用）
            userId = 1L;
            log.warn("用户未登录，使用默认用户ID: {}", userId);
        }
        
        IPage<RechargeRecordResponse> page = rechargeService.getUserRechargeRecordPage(current, size, userId);
        
        PageResult<RechargeRecordResponse> result = PageResult.of(
                page.getTotal(),
                page.getPages(),
                page.getCurrent(),
                page.getSize(),
                page.getRecords()
        );
        
        return Result.success(result, "查询成功");
    }
    
    /**
     * 获取用户余额
     */
    @GetMapping("/balance")
    @Operation(summary = "获取用户余额")
    public Result<java.math.BigDecimal> getUserBalance() {
        
        try {
            // 获取当前用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                // 临时使用默认用户ID（调试用）
                userId = 1L;
                log.warn("用户未登录，使用默认用户ID: {}", userId);
            }
            
            // 直接从数据库查询用户信息
            com.yxly.entity.SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                log.warn("用户不存在，ID: {}", userId);
                return Result.success(new java.math.BigDecimal("0.00"), "用户不存在，返回默认余额");
            }
            
            // 返回用户余额，如果为null则返回0
            java.math.BigDecimal balance = user.getBalance() != null ? 
                user.getBalance() : new java.math.BigDecimal("0.00");
            
            log.info("获取用户余额成功，用户ID: {}, 余额: {}", userId, balance);
            return Result.success(balance, "获取余额成功");
            
        } catch (Exception e) {
            log.error("获取用户余额失败", e);
            return Result.success(new java.math.BigDecimal("0.00"), "获取余额失败，返回默认值");
        }
    }
    
    /**
     * 申请提现
     */
    @PostMapping("/withdraw/apply")
    @Operation(summary = "申请提现")
    public Result<Void> applyWithdraw(@Valid @RequestBody WithdrawApplyRequest request) {
        
        try {
            // 获取当前用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                // 临时使用默认用户ID（调试用）
                userId = 1L;
                log.warn("用户未登录，使用默认用户ID: {}", userId);
            }
            
            // 检查用户余额是否足够
            com.yxly.entity.SysUser user = sysUserMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            java.math.BigDecimal balance = user.getBalance() != null ? 
                user.getBalance() : new java.math.BigDecimal("0.00");
            
            if (balance.compareTo(request.getAmount()) < 0) {
                return Result.error("余额不足，无法提现");
            }
            
            withdrawService.applyWithdraw(userId, request);
            return Result.success(null, "提现申请提交成功，请等待管理员审核");
            
        } catch (Exception e) {
            log.error("提现申请失败", e);
            return Result.error("提现申请失败，请重试");
        }
    }
    
    /**
     * 查询用户提现记录
     */
    @GetMapping("/withdraw/records")
    @Operation(summary = "查询用户提现记录")
    public Result<PageResult<com.yxly.dto.response.WithdrawRecordResponse>> getUserWithdrawRecords(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size) {
        
        try {
            // 获取当前用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                // 临时使用默认用户ID（调试用）
                userId = 1L;
                log.warn("用户未登录，使用默认用户ID: {}", userId);
            }
            
            IPage<com.yxly.dto.response.WithdrawRecordResponse> page = withdrawService.getUserWithdrawRecordPage(current, size, userId);
            
            PageResult<com.yxly.dto.response.WithdrawRecordResponse> result = PageResult.of(
                    page.getTotal(),
                    page.getPages(),
                    page.getCurrent(),
                    page.getSize(),
                    page.getRecords()
            );
            
            return Result.success(result, "查询成功");
            
        } catch (Exception e) {
            log.error("查询用户提现记录失败", e);
            return Result.error("查询失败");
        }
    }
}
