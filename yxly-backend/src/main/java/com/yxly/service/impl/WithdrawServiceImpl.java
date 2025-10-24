package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.dto.request.WithdrawApplyRequest;
import com.yxly.dto.request.WithdrawAuditRequest;
import com.yxly.dto.response.WithdrawRecordResponse;
import com.yxly.entity.WithdrawRecord;
import com.yxly.service.WithdrawService;
import com.yxly.mapper.SysUserMapper;
import com.yxly.mapper.WithdrawRecordMapper;
import com.yxly.entity.SysUser;
import com.yxly.enums.WithdrawStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 提现服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {
    
    private final SysUserMapper sysUserMapper;
    private final WithdrawRecordMapper withdrawRecordMapper;
    
    @Override
    @Transactional
    public void applyWithdraw(Long userId, WithdrawApplyRequest request) {
        
        // 获取用户信息
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查余额
        if (user.getBalance() == null || user.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        // 创建提现记录
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setUserId(userId);
        withdrawRecord.setUsername(user.getUsername());
        withdrawRecord.setAmount(request.getAmount());
        withdrawRecord.setWithdrawMethod(request.getWithdrawMethod());
        withdrawRecord.setAccountInfo(request.getAccountInfo() != null ? request.getAccountInfo() : request.getWithdrawMethod() + "账户");
        withdrawRecord.setStatus(WithdrawStatus.PENDING.getCode());
        withdrawRecord.setApplyTime(LocalDateTime.now());
        withdrawRecord.setUserRemark(request.getUserRemark());
        
        // 保存到数据库
        int result = withdrawRecordMapper.insert(withdrawRecord);
        if (result <= 0) {
            throw new RuntimeException("提现申请保存失败");
        }
        
        log.info("用户提现申请已保存 - 用户ID: {}, 用户名: {}, 金额: {}, 提现方式: {}, 记录ID: {}", 
                userId, user.getUsername(), request.getAmount(), 
                request.getWithdrawMethod(), withdrawRecord.getId());
    }
    
    @Override
    @Transactional
    public void auditWithdraw(Long auditorId, String auditorName, WithdrawAuditRequest request) {
        
        // 查询提现记录
        WithdrawRecord withdrawRecord = withdrawRecordMapper.selectById(request.getWithdrawId());
        if (withdrawRecord == null) {
            throw new RuntimeException("提现记录不存在");
        }
        
        if (withdrawRecord.getStatus() != WithdrawStatus.PENDING.getCode()) {
            throw new RuntimeException("该提现申请已经审核过了");
        }
        
        // 更新审核信息
        withdrawRecord.setStatus(request.getStatus());
        withdrawRecord.setAuditTime(LocalDateTime.now());
        withdrawRecord.setAuditorId(auditorId);
        withdrawRecord.setAuditorName(auditorName);
        withdrawRecord.setAuditRemark(request.getAuditRemark());
        
        // 如果审核通过，需要扣除用户余额
        if (request.getStatus() == WithdrawStatus.APPROVED.getCode()) {
            SysUser user = sysUserMapper.selectById(withdrawRecord.getUserId());
            if (user != null && user.getBalance() != null && 
                user.getBalance().compareTo(withdrawRecord.getAmount()) >= 0) {
                
                // 扣除余额
                user.setBalance(user.getBalance().subtract(withdrawRecord.getAmount()));
                sysUserMapper.updateById(user);
                
                log.info("提现审核通过，已扣除用户余额 - 用户ID: {}, 扣除金额: {}, 剩余余额: {}", 
                        user.getId(), withdrawRecord.getAmount(), user.getBalance());
            } else {
                throw new RuntimeException("用户余额不足，无法完成提现");
            }
        }
        
        // 更新提现记录
        int result = withdrawRecordMapper.updateById(withdrawRecord);
        if (result <= 0) {
            throw new RuntimeException("审核失败，请重试");
        }
        
        log.info("提现审核完成 - 审核人: {}, 提现ID: {}, 状态: {}, 备注: {}", 
                auditorName, request.getWithdrawId(), request.getStatus(), request.getAuditRemark());
    }
    
    @Override
    public IPage<WithdrawRecordResponse> getWithdrawRecordPage(Long current, Long size, String username, Integer status, String withdrawMethod) {
        
        // 创建分页对象
        Page<WithdrawRecord> page = new Page<>(current, size);
        
        // 查询条件
        LambdaQueryWrapper<WithdrawRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawRecord::getDeleted, 0)
                   .like(username != null && !username.trim().isEmpty(), WithdrawRecord::getUsername, username)
                   .eq(status != null, WithdrawRecord::getStatus, status)
                   .eq(withdrawMethod != null && !withdrawMethod.trim().isEmpty(), WithdrawRecord::getWithdrawMethod, withdrawMethod)
                   .orderByDesc(WithdrawRecord::getApplyTime);
        
        // 执行查询
        IPage<WithdrawRecord> recordPage = withdrawRecordMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        Page<WithdrawRecordResponse> responsePage = new Page<>(current, size);
        responsePage.setTotal(recordPage.getTotal());
        responsePage.setPages(recordPage.getPages());
        
        java.util.List<WithdrawRecordResponse> responseList = recordPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
        
        responsePage.setRecords(responseList);
        
        return responsePage;
    }
    
    @Override
    public IPage<WithdrawRecordResponse> getUserWithdrawRecordPage(Long current, Long size, Long userId) {
        
        // 创建分页对象
        Page<WithdrawRecord> page = new Page<>(current, size);
        
        // 查询条件
        LambdaQueryWrapper<WithdrawRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawRecord::getUserId, userId)
                   .eq(WithdrawRecord::getDeleted, 0)
                   .orderByDesc(WithdrawRecord::getApplyTime);
        
        // 执行查询
        IPage<WithdrawRecord> recordPage = withdrawRecordMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        Page<WithdrawRecordResponse> responsePage = new Page<>(current, size);
        responsePage.setTotal(recordPage.getTotal());
        responsePage.setPages(recordPage.getPages());
        
        java.util.List<WithdrawRecordResponse> responseList = recordPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
        
        responsePage.setRecords(responseList);
        
        return responsePage;
    }
    
    /**
     * 转换为响应DTO
     */
    private WithdrawRecordResponse convertToResponse(WithdrawRecord record) {
        WithdrawRecordResponse response = new WithdrawRecordResponse();
        response.setId(record.getId());
        response.setUserId(record.getUserId());
        response.setUsername(record.getUsername());
        response.setAmount(record.getAmount());
        response.setWithdrawMethod(record.getWithdrawMethod());
        response.setAccountInfo(record.getAccountInfo());
        response.setStatus(record.getStatus());
        response.setStatusDesc(getStatusDesc(record.getStatus()));
        response.setApplyTime(record.getApplyTime());
        response.setAuditTime(record.getAuditTime());
        response.setAuditorName(record.getAuditorName());
        response.setAuditRemark(record.getAuditRemark());
        response.setUserRemark(record.getUserRemark());
        response.setCreateTime(record.getCreateTime());
        return response;
    }
    
    /**
     * 获取状态描述
     */
    private String getStatusDesc(Integer status) {
        WithdrawStatus withdrawStatus = WithdrawStatus.getByCode(status);
        return withdrawStatus != null ? withdrawStatus.getDescription() : "未知状态";
    }
    
    @Override
    public WithdrawRecordResponse getWithdrawRecordById(Long id) {
        WithdrawRecord record = withdrawRecordMapper.selectById(id);
        if (record == null || record.getDeleted() == 1) {
            return null;
        }
        return convertToResponse(record);
    }
    
    @Override
    public IPage<WithdrawRecordResponse> getMyWithdrawRecords(Long userId, Long current, Long size, Integer status) {
        log.info("查询用户提现记录: userId={}, current={}, size={}, status={}", userId, current, size, status);
        
        // 创建分页对象
        Page<WithdrawRecord> page = new Page<>(current, size);
        
        // 查询条件
        LambdaQueryWrapper<WithdrawRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WithdrawRecord::getUserId, userId)
                   .eq(WithdrawRecord::getDeleted, 0)
                   .eq(status != null, WithdrawRecord::getStatus, status)
                   .orderByDesc(WithdrawRecord::getApplyTime);
        
        // 执行查询
        IPage<WithdrawRecord> recordPage = withdrawRecordMapper.selectPage(page, queryWrapper);
        
        // 转换为响应DTO
        Page<WithdrawRecordResponse> responsePage = new Page<>(current, size);
        responsePage.setTotal(recordPage.getTotal());
        responsePage.setPages(recordPage.getPages());
        
        java.util.List<WithdrawRecordResponse> responseList = recordPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(java.util.stream.Collectors.toList());
        
        responsePage.setRecords(responseList);
        
        return responsePage;
    }
    
    @Override
    @Transactional
    public void cancelWithdraw(Long userId, Long withdrawId) {
        log.info("用户取消提现申请: userId={}, withdrawId={}", userId, withdrawId);
        
        // 查询提现记录
        WithdrawRecord withdrawRecord = withdrawRecordMapper.selectById(withdrawId);
        if (withdrawRecord == null || withdrawRecord.getDeleted() == 1) {
            throw new RuntimeException("提现记录不存在");
        }
        
        // 验证是否是本人的记录
        if (!withdrawRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人的提现申请");
        }
        
        // 只能取消待审核的申请
        if (withdrawRecord.getStatus() != WithdrawStatus.PENDING.getCode()) {
            throw new RuntimeException("只能取消待审核的提现申请");
        }
        
        // 标记为已取消
        withdrawRecord.setStatus(WithdrawStatus.REJECTED.getCode()); // 使用拒绝状态表示取消
        withdrawRecord.setAuditTime(LocalDateTime.now());
        withdrawRecord.setAuditRemark("用户主动取消");
        
        int result = withdrawRecordMapper.updateById(withdrawRecord);
        if (result <= 0) {
            throw new RuntimeException("取消失败，请重试");
        }
        
        log.info("提现申请已取消: withdrawId={}", withdrawId);
    }
    
    @Override
    public WithdrawRecordResponse getMyWithdrawRecordById(Long userId, Long withdrawId) {
        log.info("查询用户提现记录详情: userId={}, withdrawId={}", userId, withdrawId);
        
        WithdrawRecord record = withdrawRecordMapper.selectById(withdrawId);
        if (record == null || record.getDeleted() == 1) {
            return null;
        }
        
        // 验证是否是本人的记录
        if (!record.getUserId().equals(userId)) {
            log.warn("用户尝试访问他人的提现记录: userId={}, recordUserId={}", userId, record.getUserId());
            return null;
        }
        
        return convertToResponse(record);
    }
}
