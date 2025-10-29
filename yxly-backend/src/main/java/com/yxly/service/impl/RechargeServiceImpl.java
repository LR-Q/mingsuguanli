package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.common.ResultCode;
import com.yxly.dto.request.RechargeApplyRequest;
import com.yxly.dto.request.RechargeAuditRequest;
import com.yxly.dto.response.RechargeRecordResponse;
import com.yxly.entity.RechargeRecord;
import com.yxly.entity.SysUser;
import com.yxly.enums.RechargeStatus;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.RechargeRecordMapper;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.RechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 充值服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeServiceImpl implements RechargeService {
    
    private final RechargeRecordMapper rechargeRecordMapper;
    private final SysUserMapper sysUserMapper;
    
    // 状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put(0, "待审核");
        STATUS_MAP.put(1, "审核通过");
        STATUS_MAP.put(2, "审核拒绝");
    }
    
    @Override
    @Transactional
    public void applyRecharge(Long userId, RechargeApplyRequest request) {
        log.info("=== 开始处理充值申请 ===");
        log.info("接收到的userId: {}", userId);
        log.info("充值金额: {}", request.getAmount());
        
        // 获取用户信息
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            log.error("用户不存在: userId={}", userId);
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        log.info("查询到的用户信息: id={}, username={}, realName={}", 
                user.getId(), user.getUsername(), user.getRealName());
        
        // 创建充值记录
        RechargeRecord record = new RechargeRecord();
        record.setUserId(userId);
        record.setUsername(user.getUsername());
        record.setAmount(request.getAmount());
        record.setPaymentMethod(request.getPaymentMethod());
        record.setPaymentProof(request.getPaymentProof());
        record.setStatus(RechargeStatus.PENDING.getCode());
        record.setApplyTime(LocalDateTime.now());
        record.setUserRemark(request.getUserRemark());
        
        log.info("准备保存充值记录: userId={}, username={}", 
                record.getUserId(), record.getUsername());
        
        int result = rechargeRecordMapper.insert(record);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "充值申请提交失败");
        }
        
        log.info("充值记录保存成功: recordId={}, userId={}, username={}", 
                record.getId(), record.getUserId(), record.getUsername());
        log.info("=== 充值申请处理完成 ===");
    }
    
    @Override
    @Transactional
    public void auditRecharge(Long auditorId, String auditorName, RechargeAuditRequest request) {
        log.info("管理员审核充值: auditorId={}, recordId={}, status={}", 
                auditorId, request.getId(), request.getStatus());
        
        // 获取充值记录
        RechargeRecord record = rechargeRecordMapper.selectById(request.getId());
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "充值记录不存在");
        }
        
        // 检查状态
        if (!RechargeStatus.PENDING.getCode().equals(record.getStatus())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "该充值记录已审核，无法重复操作");
        }
        
        // 验证审核状态
        if (!RechargeStatus.APPROVED.getCode().equals(request.getStatus()) && 
            !RechargeStatus.REJECTED.getCode().equals(request.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核状态无效");
        }
        
        // 更新充值记录
        record.setStatus(request.getStatus());
        record.setAuditTime(LocalDateTime.now());
        record.setAuditorId(auditorId);
        record.setAuditorName(auditorName);
        record.setAuditRemark(request.getAuditRemark());
        
        int result = rechargeRecordMapper.updateById(record);
        if (result <= 0) {
            throw new BusinessException(ResultCode.OPERATION_FAILED, "充值审核失败");
        }
        
        // 如果审核通过，更新用户余额
        if (RechargeStatus.APPROVED.getCode().equals(request.getStatus())) {
            SysUser user = sysUserMapper.selectById(record.getUserId());
            if (user != null) {
                BigDecimal currentBalance = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
                user.setBalance(currentBalance.add(record.getAmount()));
                sysUserMapper.updateById(user);
                log.info("用户余额更新成功: userId={}, oldBalance={}, newBalance={}", 
                        user.getId(), currentBalance, user.getBalance());
            }
        }
        
        log.info("充值审核完成: recordId={}, status={}", record.getId(), request.getStatus());
    }
    
    @Override
    public IPage<RechargeRecordResponse> getRechargeRecordPage(Long current, Long size, 
                                                              String username, Integer status, String paymentMethod) {
        log.info("分页查询充值记录: current={}, size={}, username={}, status={}, paymentMethod={}", 
                current, size, username, status, paymentMethod);
        
        Page<RechargeRecordResponse> page = new Page<>(current, size);
        IPage<RechargeRecordResponse> result = rechargeRecordMapper.selectRechargeRecordPage(
                page, username, status, paymentMethod);
        
        // 设置状态名称
        result.getRecords().forEach(record -> {
            record.setStatusName(STATUS_MAP.get(record.getStatus()));
        });
        
        return result;
    }
    
    @Override
    public IPage<RechargeRecordResponse> getUserRechargeRecordPage(Long current, Long size, Long userId) {
        log.info("分页查询用户充值记录: current={}, size={}, userId={}", current, size, userId);
        
        Page<RechargeRecordResponse> page = new Page<>(current, size);
        IPage<RechargeRecordResponse> result = rechargeRecordMapper.selectUserRechargeRecordPage(page, userId);
        
        // 设置状态名称
        result.getRecords().forEach(record -> {
            record.setStatusName(STATUS_MAP.get(record.getStatus()));
        });
        
        return result;
    }
    
    @Override
    public RechargeRecordResponse getRechargeRecordById(Long id) {
        log.info("获取充值记录详情: id={}", id);
        
        RechargeRecord record = rechargeRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "充值记录不存在");
        }
        
        RechargeRecordResponse response = new RechargeRecordResponse();
        BeanUtils.copyProperties(record, response);
        response.setStatusName(STATUS_MAP.get(record.getStatus()));
        
        return response;
    }
}








