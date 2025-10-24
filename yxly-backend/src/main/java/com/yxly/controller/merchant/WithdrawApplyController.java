package com.yxly.controller.merchant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.request.WithdrawApplyRequest;
import com.yxly.dto.response.WithdrawRecordResponse;
import com.yxly.service.WithdrawService;
import com.yxly.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 民宿主-提现申请控制器
 */
@RestController
@RequestMapping("/v1/merchant/withdraw")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "民宿主-提现申请", description = "民宿主提现申请相关接口")
public class WithdrawApplyController {
    
    private final WithdrawService withdrawService;
    private final SecurityUtils securityUtils;
    
    /**
     * 查询自己的提现记录
     */
    @GetMapping("/my-records")
    @Operation(summary = "查询自己的提现记录", description = "民宿主查看自己的提现记录")
    public Result<PageResult<WithdrawRecordResponse>> getMyWithdrawRecords(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        
        try {
            // 获取当前登录的民宿主用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            log.info("民宿主查询自己的提现记录: userId={}, current={}, size={}", userId, current, size);
            
            IPage<WithdrawRecordResponse> page = withdrawService.getMyWithdrawRecords(
                userId, current, size, status);
            
            PageResult<WithdrawRecordResponse> result = PageResult.of(
                    page.getTotal(),
                    page.getPages(),
                    page.getCurrent(),
                    page.getSize(),
                    page.getRecords()
            );
            
            return Result.success(result, "查询成功");
            
        } catch (Exception e) {
            log.error("查询提现记录失败", e);
            return Result.error("查询失败");
        }
    }
    
    /**
     * 申请提现
     */
    @PostMapping("/apply")
    @Operation(summary = "申请提现", description = "民宿主申请提现")
    public Result<Void> applyWithdraw(@Valid @RequestBody WithdrawApplyRequest request) {
        
        try {
            // 获取当前登录的民宿主用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            log.info("民宿主申请提现: userId={}, amount={}", userId, request.getAmount());
            
            withdrawService.applyWithdraw(userId, request);
            
            return Result.success(null, "提现申请提交成功，请等待审核");
            
        } catch (Exception e) {
            log.error("提现申请失败", e);
            return Result.error(e.getMessage() != null ? e.getMessage() : "申请失败，请重试");
        }
    }
    
    /**
     * 取消提现申请（仅限待审核状态）
     */
    @PostMapping("/cancel/{id}")
    @Operation(summary = "取消提现申请", description = "民宿主取消自己的待审核提现申请")
    public Result<Void> cancelWithdraw(@PathVariable Long id) {
        
        try {
            // 获取当前登录的民宿主用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            log.info("民宿主取消提现申请: userId={}, withdrawId={}", userId, id);
            
            withdrawService.cancelWithdraw(userId, id);
            
            return Result.success(null, "提现申请已取消");
            
        } catch (Exception e) {
            log.error("取消提现申请失败", e);
            return Result.error(e.getMessage() != null ? e.getMessage() : "取消失败");
        }
    }
    
    /**
     * 获取提现记录详情（仅能查看自己的）
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取提现记录详情")
    public Result<WithdrawRecordResponse> getWithdrawRecordById(@PathVariable Long id) {
        
        try {
            // 获取当前登录的民宿主用户ID
            Long userId = securityUtils.getCurrentUserId();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            log.info("民宿主查询提现记录详情: userId={}, withdrawId={}", userId, id);
            
            WithdrawRecordResponse record = withdrawService.getMyWithdrawRecordById(userId, id);
            if (record == null) {
                return Result.error("提现记录不存在或无权访问");
            }
            return Result.success(record, "查询成功");
            
        } catch (Exception e) {
            log.error("查询提现记录详情失败", e);
            return Result.error("查询失败");
        }
    }
}
