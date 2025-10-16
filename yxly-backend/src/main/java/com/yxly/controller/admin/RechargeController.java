package com.yxly.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.request.RechargeAuditRequest;
import com.yxly.dto.response.RechargeRecordResponse;
import com.yxly.service.RechargeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 管理员充值管理控制器
 */
@RestController
@RequestMapping("/v1/admin/recharge")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员充值管理", description = "管理员充值审核相关接口")
public class RechargeController {
    
    private final RechargeService rechargeService;
    
    /**
     * 分页查询充值记录
     */
    @GetMapping
    @Operation(summary = "分页查询充值记录")
    public Result<PageResult<RechargeRecordResponse>> getRechargeRecordPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "支付方式") @RequestParam(required = false) String paymentMethod) {
        
        IPage<RechargeRecordResponse> page = rechargeService.getRechargeRecordPage(
                current, size, username, status, paymentMethod);
        
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
     * 获取充值记录详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取充值记录详情")
    public Result<RechargeRecordResponse> getRechargeRecordById(
            @Parameter(description = "充值记录ID") @PathVariable Long id) {
        
        RechargeRecordResponse record = rechargeService.getRechargeRecordById(id);
        return Result.success(record, "查询成功");
    }
    
    /**
     * 审核充值申请
     */
    @PostMapping("/audit")
    @Operation(summary = "审核充值申请")
    public Result<Void> auditRecharge(
            @Valid @RequestBody RechargeAuditRequest request,
            Authentication authentication) {
        
        // 获取当前管理员信息
        String auditorName = authentication.getName();
        // 这里简化处理，实际应该从用户信息中获取ID
        Long auditorId = 1L; // 临时使用固定值，实际应该从authentication中获取
        
        rechargeService.auditRecharge(auditorId, auditorName, request);
        return Result.success(null, "审核成功");
    }
}
