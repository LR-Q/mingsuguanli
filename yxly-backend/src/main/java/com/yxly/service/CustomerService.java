package com.yxly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.dto.response.CustomerResponse;

/**
 * 客户管理服务接口
 * 
 * @author yxly
 * @since 2024-01-01
 */
public interface CustomerService {
    
    /**
     * 分页查询客户列表
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 搜索关键词（用户名、真实姓名、手机号、邮箱）
     * @return 客户分页数据
     */
    Page<CustomerResponse> getCustomerPage(Long current, Long size, String keyword);
    
    /**
     * 根据ID获取客户详情
     * 
     * @param id 客户ID
     * @return 客户详情
     */
    CustomerResponse getCustomerById(Long id);
    
    /**
     * 更新客户状态
     * 
     * @param id 客户ID
     * @param status 状态（0:禁用 1:启用）
     */
    void updateCustomerStatus(Long id, Integer status);
    
    /**
     * 删除客户（逻辑删除）
     * 
     * @param id 客户ID
     */
    void deleteCustomer(Long id);
}
