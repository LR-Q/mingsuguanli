package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.exception.BusinessException;
import com.yxly.common.ResultCode;
import com.yxly.dto.response.CustomerResponse;
import com.yxly.entity.SysUser;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 客户管理服务实现
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    
    private final SysUserMapper userMapper;
    
    @Override
    public Page<CustomerResponse> getCustomerPage(Long current, Long size, String keyword) {
        log.info("分页查询客户列表: current={}, size={}, keyword={}", current, size, keyword);
        
        // 构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getRoleId, 2L) // 只查询普通用户（role_id=2）
            .eq(SysUser::getDeleted, 0); // 未删除的用户
        
        // 添加搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(SysUser::getUsername, keyword)
                .or().like(SysUser::getRealName, keyword)
                .or().like(SysUser::getPhone, keyword)
                .or().like(SysUser::getEmail, keyword)
            );
        }
        
        // 按创建时间倒序
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        
        // 分页查询
        Page<SysUser> userPage = new Page<>(current, size);
        userPage = userMapper.selectPage(userPage, queryWrapper);
        
        // 转换为响应DTO
        Page<CustomerResponse> responsePage = new Page<>();
        BeanUtils.copyProperties(userPage, responsePage, "records");
        
        responsePage.setRecords(
            userPage.getRecords().stream()
                .map(this::convertToResponse)
                .toList()
        );
        
        log.info("查询到客户数量: {}", responsePage.getTotal());
        return responsePage;
    }
    
    @Override
    public CustomerResponse getCustomerById(Long id) {
        log.info("获取客户详情: id={}", id);
        
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getRoleId, 2L) // 只能查询普通用户
                .eq(SysUser::getDeleted, 0)
        );
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "客户不存在");
        }
        
        return convertToResponse(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerStatus(Long id, Integer status) {
        log.info("更新客户状态: id={}, status={}", id, status);
        
        // 验证状态值
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "状态值无效");
        }
        
        // 查询客户
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getRoleId, 2L) // 只能操作普通用户
                .eq(SysUser::getDeleted, 0)
        );
        
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "客户不存在");
        }
        
        // 更新状态
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        
        int updateResult = userMapper.updateById(user);
        if (updateResult <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "状态更新失败");
        }
        
        log.info("客户状态更新成功: username={}, status={}", user.getUsername(), status);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        log.info("=== Service开始删除客户: id={}", id);
        System.out.println("=== Service开始删除客户: id=" + id);
        
        // 查询客户
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, id)
                .eq(SysUser::getRoleId, 2L) // 只能删除普通用户
                .eq(SysUser::getDeleted, 0)
        );
        
        System.out.println("=== 查询到的用户: " + (user != null ? user.getUsername() + ", deleted=" + user.getDeleted() : "null"));
        
        if (user == null) {
            System.out.println("=== 用户不存在，抛出异常");
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "客户不存在");
        }
        
        // 逻辑删除 - 使用MyBatis-Plus的逻辑删除
        System.out.println("=== 开始逻辑删除操作");
        int updateResult = userMapper.deleteById(id);
        System.out.println("=== 逻辑删除结果: " + updateResult);
        
        if (updateResult <= 0) {
            System.out.println("=== 更新失败，抛出异常");
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "客户删除失败");
        }
        
        System.out.println("=== Service删除成功: username=" + user.getUsername());
        log.info("客户删除成功: username={}", user.getUsername());
    }
    
    /**
     * 转换为响应DTO
     */
    private CustomerResponse convertToResponse(SysUser user) {
        CustomerResponse response = new CustomerResponse();
        BeanUtils.copyProperties(user, response);
        
        // 设置状态描述
        response.setStatusText(user.getStatus() == 1 ? "正常" : "禁用");
        
        return response;
    }
}
