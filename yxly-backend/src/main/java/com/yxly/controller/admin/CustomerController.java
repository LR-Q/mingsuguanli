package com.yxly.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.common.PageResult;
import com.yxly.common.Result;
import com.yxly.dto.response.CustomerResponse;
import com.yxly.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理控制器
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/v1/admin/customers")
@RequiredArgsConstructor
@Tag(name = "客户管理", description = "管理端客户信息管理接口")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @Operation(summary = "分页查询客户列表", description = "管理员查询所有注册用户（除admin外）")
    @GetMapping
    public Result<PageResult<CustomerResponse>> getCustomerPage(
            @Parameter(description = "当前页", example = "1") 
            @RequestParam(defaultValue = "1") Long current,
            
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") Long size,
            
            @Parameter(description = "搜索关键词（用户名、姓名、手机号、邮箱）") 
            @RequestParam(required = false) String keyword) {
        
        log.info("管理员查询客户列表: current={}, size={}, keyword={}", current, size, keyword);
        
        Page<CustomerResponse> page = customerService.getCustomerPage(current, size, keyword);
        
        PageResult<CustomerResponse> pageResult = PageResult.of(
            page.getTotal(),
            page.getPages(),
            page.getCurrent(),
            page.getSize(),
            page.getRecords()
        );
        
        return Result.success(pageResult, "查询成功");
    }
    
    @Operation(summary = "获取客户详情", description = "根据ID获取客户详细信息")
    @GetMapping("/{id}")
    public Result<CustomerResponse> getCustomerById(
            @Parameter(description = "客户ID") 
            @PathVariable Long id) {
        
        log.info("管理员查询客户详情: id={}", id);
        
        CustomerResponse customer = customerService.getCustomerById(id);
        return Result.success(customer, "查询成功");
    }
    
    @Operation(summary = "更新客户状态", description = "启用或禁用客户账号")
    @PutMapping("/{id}/status")
    public Result<Void> updateCustomerStatus(
            @Parameter(description = "客户ID") 
            @PathVariable Long id,
            
            @Parameter(description = "状态（0:禁用 1:启用）") 
            @RequestParam Integer status) {
        
        log.info("管理员更新客户状态: id={}, status={}", id, status);
        
        customerService.updateCustomerStatus(id, status);
        return Result.success(null, "状态更新成功");
    }
    
    @Operation(summary = "删除客户", description = "逻辑删除客户账号")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCustomer(
            @Parameter(description = "客户ID") 
            @PathVariable Long id) {
        
        log.info("=== 收到删除请求 === 客户ID: {}", id);
        System.out.println("=== 收到删除请求 === 客户ID: " + id);
        
        try {
            customerService.deleteCustomer(id);
            log.info("=== 删除成功 === 客户ID: {}", id);
            System.out.println("=== 删除成功 === 客户ID: " + id);
            return Result.success(null, "删除成功");
        } catch (Exception e) {
            log.error("=== 删除失败 === 客户ID: {}, 错误: {}", id, e.getMessage());
            System.out.println("=== 删除失败 === 客户ID: " + id + ", 错误: " + e.getMessage());
            throw e;
        }
    }
}
