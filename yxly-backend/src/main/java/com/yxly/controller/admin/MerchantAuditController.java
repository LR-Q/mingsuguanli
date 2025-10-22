package com.yxly.controller.admin;

import com.yxly.common.Result;
import com.yxly.dto.request.MerchantAuditRequest;
import com.yxly.dto.response.MerchantAuditVO;
import com.yxly.service.MerchantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 超级管理员-商户审核控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/super-admin/merchants")
@RequiredArgsConstructor
@Tag(name = "超级管理员-商户审核", description = "商户注册申请审核相关接口")
public class MerchantAuditController {
    
    private final MerchantService merchantService;
    
    /**
     * 获取待审核商户列表
     */
    @GetMapping("/pending")
    @Operation(summary = "获取待审核商户列表", description = "查询所有待审核的商户注册申请")
    public Result<List<MerchantAuditVO>> getPendingMerchants() {
        log.info("查询待审核商户列表");
        List<MerchantAuditVO> list = merchantService.getPendingMerchants();
        return Result.success(list);
    }
    
    /**
     * 获取所有商户列表（包含所有状态）
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有商户列表", description = "查询所有商户，包含待审核、已通过、已拒绝")
    public Result<List<MerchantAuditVO>> getAllMerchants(
            @Parameter(description = "审核状态(0:待审核 1:已认证 2:已拒绝，不传则查询全部)")
            @RequestParam(required = false) Integer auditStatus) {
        log.info("查询所有商户列表, auditStatus={}", auditStatus);
        List<MerchantAuditVO> list = merchantService.getAllMerchants(auditStatus);
        return Result.success(list);
    }
    
    /**
     * 审核商户
     */
    @PostMapping("/audit")
    @Operation(summary = "审核商户", description = "审核商户注册申请（通过或拒绝）")
    public Result<Void> auditMerchant(@Valid @RequestBody MerchantAuditRequest request) {
        log.info("审核商户: merchantId={}, auditStatus={}", request.getMerchantId(), request.getAuditStatus());
        merchantService.auditMerchant(request);
        return Result.success(null, "审核成功");
    }
    
    /**
     * 获取商户详情
     */
    @GetMapping("/{merchantId}")
    @Operation(summary = "获取商户详情", description = "查询商户详细信息")
    public Result<MerchantAuditVO> getMerchantDetail(
            @Parameter(description = "商户ID", required = true)
            @PathVariable Long merchantId) {
        log.info("查询商户详情: merchantId={}", merchantId);
        MerchantAuditVO detail = merchantService.getMerchantDetail(merchantId);
        return Result.success(detail);
    }
}
