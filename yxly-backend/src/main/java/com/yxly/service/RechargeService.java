package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.dto.request.RechargeApplyRequest;
import com.yxly.dto.request.RechargeAuditRequest;
import com.yxly.dto.response.RechargeRecordResponse;

/**
 * 充值服务接口
 */
public interface RechargeService {
    
    /**
     * 用户申请充值
     */
    void applyRecharge(Long userId, RechargeApplyRequest request);
    
    /**
     * 管理员审核充值
     */
    void auditRecharge(Long auditorId, String auditorName, RechargeAuditRequest request);
    
    /**
     * 分页查询充值记录（管理员）
     */
    IPage<RechargeRecordResponse> getRechargeRecordPage(Long current, Long size, 
                                                       String username, Integer status, String paymentMethod);
    
    /**
     * 分页查询用户充值记录
     */
    IPage<RechargeRecordResponse> getUserRechargeRecordPage(Long current, Long size, Long userId);
    
    /**
     * 根据ID获取充值记录详情
     */
    RechargeRecordResponse getRechargeRecordById(Long id);
}
