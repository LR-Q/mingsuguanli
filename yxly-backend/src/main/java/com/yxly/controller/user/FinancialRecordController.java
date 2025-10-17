package com.yxly.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.common.Result;
import com.yxly.entity.FinancialRecord;
import com.yxly.util.SecurityUtils;
import com.yxly.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户财务记录控制器
 */
@Slf4j
@Tag(name = "用户财务记录", description = "用户财务记录相关接口")
@RestController
@RequestMapping("/v1/user/financial-records")
@RequiredArgsConstructor
public class FinancialRecordController {
    
    private final FinancialRecordService financialRecordService;
    private final SecurityUtils securityUtils;
    
    @Operation(summary = "获取我的财务记录")
    @GetMapping("/my")
    public Result<IPage<FinancialRecord>> getMyFinancialRecords(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类筛选") @RequestParam(required = false) String category
    ) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            log.info("用户获取财务记录: userId={}, current={}, size={}, category={}", userId, current, size, category);
            
            IPage<FinancialRecord> records = financialRecordService.getUserFinancialRecords(userId, current, size, category);
            return Result.success(records);
        } catch (Exception e) {
            log.error("获取财务记录失败", e);
            return Result.error(e.getMessage());
        }
    }
}
