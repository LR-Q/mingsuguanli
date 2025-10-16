package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.dto.request.WithdrawApplyRequest;
import com.yxly.dto.request.WithdrawAuditRequest;
import com.yxly.dto.response.WithdrawRecordResponse;

/**
 * 提现服务接口
 */
public interface WithdrawService {
    
    /**
     * 用户申请提现
     */
    void applyWithdraw(Long userId, WithdrawApplyRequest request);
    
    /**
     * 管理员审核提现
     */
    void auditWithdraw(Long auditorId, String auditorName, WithdrawAuditRequest request);
    
    /**
     * 分页查询提现记录（管理员）
     */
    IPage<WithdrawRecordResponse> getWithdrawRecordPage(Long current, Long size, 
                                                       String username, Integer status, String withdrawMethod);
    
    /**
     * 分页查询用户提现记录
     */
    IPage<WithdrawRecordResponse> getUserWithdrawRecordPage(Long current, Long size, Long userId);
    
    /**
     * 根据ID获取提现记录详情
     */
    WithdrawRecordResponse getWithdrawRecordById(Long id);
}
