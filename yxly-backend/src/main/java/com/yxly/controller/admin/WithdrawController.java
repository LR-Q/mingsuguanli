package com.yxly.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.request.WithdrawAuditRequest;
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
 * 管理员提现管理控制器
 */
@RestController
@RequestMapping("/v1/admin/withdraw")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员提现管理", description = "管理员提现审核相关接口")
public class WithdrawController {
    
    private final WithdrawService withdrawService;
    private final SecurityUtils securityUtils;
    
    /**
     * 分页查询提现记录
     */
    @GetMapping
    @Operation(summary = "分页查询提现记录")
    public Result<PageResult<WithdrawRecordResponse>> getWithdrawRecords(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "提现方式") @RequestParam(required = false) String withdrawMethod) {
        
        try {
            IPage<WithdrawRecordResponse> page = withdrawService.getWithdrawRecordPage(
                current, size, username, status, withdrawMethod);
            
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
     * 根据ID获取提现记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取提现记录详情")
    public Result<WithdrawRecordResponse> getWithdrawRecordById(
            @Parameter(description = "提现记录ID") @PathVariable Long id) {
        
        try {
            WithdrawRecordResponse record = withdrawService.getWithdrawRecordById(id);
            if (record == null) {
                return Result.error("提现记录不存在");
            }
            return Result.success(record, "查询成功");
            
        } catch (Exception e) {
            log.error("查询提现记录详情失败", e);
            return Result.error("查询失败");
        }
    }
    
    /**
     * 审核提现申请
     */
    @PostMapping("/audit")
    @Operation(summary = "审核提现申请")
    public Result<Void> auditWithdraw(@Valid @RequestBody WithdrawAuditRequest request) {
        
        try {
            // 获取当前管理员信息
            Long auditorId = securityUtils.getCurrentUserId();
            String auditorName = "admin"; // 临时使用固定值
            
            if (auditorId == null) {
                // 临时使用默认管理员ID（调试用）
                auditorId = 1L;
                log.warn("管理员未登录，使用默认管理员ID: {}", auditorId);
            }
            
            withdrawService.auditWithdraw(auditorId, auditorName, request);
            
            String statusText = request.getStatus() == 1 ? "通过" : "拒绝";
            return Result.success(null, "提现审核" + statusText + "成功");
            
        } catch (Exception e) {
            log.error("提现审核失败", e);
            return Result.error("审核失败，请重试");
        }
    }
}
