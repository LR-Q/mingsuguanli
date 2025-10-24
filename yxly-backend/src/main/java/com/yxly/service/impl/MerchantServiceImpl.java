package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxly.common.ResultCode;
import com.yxly.dto.request.AdminResetPasswordRequest;
import com.yxly.dto.request.LoginRequest;
import com.yxly.dto.request.MerchantAuditRequest;
import com.yxly.dto.request.MerchantRegisterRequest;
import com.yxly.dto.response.LoginResponse;
import com.yxly.dto.response.MerchantAuditVO;
import com.yxly.dto.response.MerchantSimpleVO;
import com.yxly.entity.MerchantInfo;
import com.yxly.entity.SysRole;
import com.yxly.entity.SysUser;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.MerchantInfoMapper;
import com.yxly.mapper.SysRoleMapper;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.AuthService;
import com.yxly.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商户服务实现
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    
    private final MerchantInfoMapper merchantInfoMapper;
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse registerMerchant(MerchantRegisterRequest request) {
        log.info("民宿主管理员注册: username={}, realName={}", request.getUsername(), request.getRealName());
        
        // 1. 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次输入的密码不一致");
        }
        
        // 2. 检查用户名是否已存在
        if (authService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS, "用户名已存在");
        }
        
        // 3. 检查手机号是否已存在
        if (authService.existsByPhone(request.getPhone())) {
            throw new BusinessException(ResultCode.PHONE_ALREADY_EXISTS, "手机号已被注册");
        }
        
        // 4. 获取民宿主管理员角色
        SysRole merchantRole = roleMapper.selectOne(
            new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, "HOMESTAY_ADMIN")
                .eq(SysRole::getDeleted, 0)
        );
        
        if (merchantRole == null) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "系统角色配置错误，请联系管理员");
        }
        
        // 5. 创建用户账号
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setRoleId(merchantRole.getId());
        user.setStatus(0); // 待审核状态，初始禁用
        user.setMerchantStatus(0); // 待审核
        user.setApplyTime(LocalDateTime.now());
        user.setEmailVerified(0);
        user.setPhoneVerified(0);
        
        userMapper.insert(user);
        
        // 6. 生成商户编码（使用用户名+时间戳）
        String merchantCode = generateMerchantCode(request.getUsername());
        
        // 7. 创建商户信息
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setAdminUserId(user.getId());
        merchantInfo.setMerchantName(request.getRealName() + "的民宿"); // 默认使用姓名+的民宿
        merchantInfo.setMerchantCode(merchantCode);
        
        // 联系信息（使用用户信息）
        merchantInfo.setContactPerson(request.getRealName());
        merchantInfo.setContactPhone(request.getPhone());
        
        // 资质证明
        merchantInfo.setIdCardFront(request.getIdCardFront());
        merchantInfo.setIdCardBack(request.getIdCardBack());
        
        // 审核信息
        merchantInfo.setAuditStatus(0); // 待审核
        merchantInfo.setSettlementRate(new BigDecimal("100.00")); // 默认结算比例100%
        merchantInfo.setStatus(1); // 启用
        
        // 统计信息初始化
        merchantInfo.setRoomCount(0);
        merchantInfo.setOrderCount(0);
        merchantInfo.setTotalRevenue(BigDecimal.ZERO);
        
        merchantInfoMapper.insert(merchantInfo);
        
        // 10. 更新用户的merchant_id
        user.setMerchantId(merchantInfo.getId());
        userMapper.updateById(user);
        
        log.info("民宿主管理员注册成功: merchantId={}, userId={}, username={}, realName={}", 
                 merchantInfo.getId(), user.getId(), user.getUsername(), user.getRealName());
        
        // 11. 返回提示信息（不自动登录，因为需要审核）
        LoginResponse response = new LoginResponse();
        response.setAccessToken(null);
        response.setRefreshToken(null);
        
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .roleId(user.getRoleId())
            .build();
        response.setUserInfo(userInfo);
        
        return response;
    }
    
    @Override
    public boolean existsByMerchantName(String merchantName) {
        return merchantInfoMapper.selectCount(
            new LambdaQueryWrapper<MerchantInfo>()
                .eq(MerchantInfo::getMerchantName, merchantName)
                .eq(MerchantInfo::getDeleted, 0)
        ) > 0;
    }
    
    /**
     * 生成商户编码
     * 规则: M + 时间戳 + 随机数
     */
    private String generateMerchantCode(String username) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        int random = (int) (Math.random() * 100);
        return String.format("M%s%02d", timestamp, random);
    }
    
    @Override
    public List<MerchantAuditVO> getPendingMerchants() {
        log.info("查询待审核商户列表");
        
        // 查询audit_status=0的商户信息
        List<MerchantInfo> merchantList = merchantInfoMapper.selectList(
                new LambdaQueryWrapper<MerchantInfo>()
                        .eq(MerchantInfo::getAuditStatus, 0)
                        .eq(MerchantInfo::getDeleted, 0)
                        .orderByDesc(MerchantInfo::getCreateTime)
        );
        
        return merchantList.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    public List<MerchantAuditVO> getAllMerchants(Integer auditStatus) {
        log.info("查询所有商户列表, auditStatus={}", auditStatus);
        
        LambdaQueryWrapper<MerchantInfo> wrapper = new LambdaQueryWrapper<MerchantInfo>()
                .eq(MerchantInfo::getDeleted, 0)
                .orderByDesc(MerchantInfo::getCreateTime);
        
        // 如果指定了审核状态，添加条件
        if (auditStatus != null) {
            wrapper.eq(MerchantInfo::getAuditStatus, auditStatus);
        }
        
        List<MerchantInfo> merchantList = merchantInfoMapper.selectList(wrapper);
        return merchantList.stream().map(this::convertToVO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMerchant(MerchantAuditRequest request) {
        log.info("审核商户: merchantId={}, auditStatus={}", request.getMerchantId(), request.getAuditStatus());
        
        // 1. 查询商户信息
        MerchantInfo merchantInfo = merchantInfoMapper.selectById(request.getMerchantId());
        if (merchantInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商户不存在");
        }
        
        if (merchantInfo.getAuditStatus() != 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "该商户已审核，无法重复审核");
        }
        
        // 2. 更新商户审核状态
        merchantInfo.setAuditStatus(request.getAuditStatus());
        merchantInfo.setAuditTime(LocalDateTime.now());
        merchantInfo.setAuditRemarks(request.getAuditRemarks());
        // TODO: 设置审核人ID（从当前登录用户获取）
        // merchantInfo.setAuditorId(SecurityUtils.getCurrentUserId());
        
        merchantInfoMapper.updateById(merchantInfo);
        
        // 3. 如果审核通过，更新用户状态和角色
        if (request.getAuditStatus() == 1) {
            SysUser user = userMapper.selectById(merchantInfo.getAdminUserId());
            if (user != null) {
                // 更新用户商户状态为已认证
                user.setMerchantStatus(1);
                user.setMerchantId(merchantInfo.getId());
                user.setStatus(1);  // 启用用户账号
                
                // 更新用户角色为民宿主管理员
                SysRole homestayAdminRole = roleMapper.selectOne(
                        new LambdaQueryWrapper<SysRole>()
                                .eq(SysRole::getRoleCode, "HOMESTAY_ADMIN")
                                .eq(SysRole::getDeleted, 0)
                );
                if (homestayAdminRole != null) {
                    user.setRoleId(homestayAdminRole.getId());
                }
                
                userMapper.updateById(user);
                log.info("审核通过，已更新用户角色和商户状态，已启用账号: userId={}", user.getId());
            }
        } else if (request.getAuditStatus() == 2) {
            // 审核拒绝，更新用户商户状态为已拒绝，保持账号禁用状态
            SysUser user = userMapper.selectById(merchantInfo.getAdminUserId());
            if (user != null) {
                user.setMerchantStatus(2);
                user.setStatus(0);  // 保持禁用状态
                userMapper.updateById(user);
                log.info("审核拒绝，已更新用户商户状态，保持账号禁用: userId={}", user.getId());
            }
        }
        
        log.info("商户审核完成: merchantId={}, auditStatus={}", request.getMerchantId(), request.getAuditStatus());
    }
    
    @Override
    public MerchantAuditVO getMerchantDetail(Long merchantId) {
        log.info("查询商户详情: merchantId={}", merchantId);
        
        MerchantInfo merchantInfo = merchantInfoMapper.selectById(merchantId);
        if (merchantInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商户不存在");
        }
        
        return convertToVO(merchantInfo);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetMerchantPassword(AdminResetPasswordRequest request) {
        log.info("超级管理员重置商户密码: merchantId={}", request.getMerchantId());
        
        // 查询商户信息
        MerchantInfo merchantInfo = merchantInfoMapper.selectById(request.getMerchantId());
        if (merchantInfo == null || merchantInfo.getDeleted() == 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商户不存在");
        }
        
        // 查询商户关联的管理员用户
        SysUser user = userMapper.selectById(merchantInfo.getAdminUserId());
        if (user == null || user.getDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND, "商户管理员账号不存在");
        }
        
        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED, "商户账号已被禁用");
        }
        
        // 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        
        // 更新密码
        int updateResult = userMapper.updateById(user);
        if (updateResult <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "密码重置失败，请稍后重试");
        }
        
        log.info("商户密码重置成功: merchantId={}, username={}, merchantName={}", 
                request.getMerchantId(), user.getUsername(), merchantInfo.getMerchantName());
    }
    
    /**
     * 转换为VO对象
     */
    private MerchantAuditVO convertToVO(MerchantInfo merchantInfo) {
        MerchantAuditVO vo = new MerchantAuditVO();
        BeanUtils.copyProperties(merchantInfo, vo);
        
        // 查询用户信息
        SysUser user = userMapper.selectById(merchantInfo.getAdminUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            vo.setApplyTime(user.getApplyTime());
        }
        
        return vo;
    }
    
    @Override
    public List<MerchantSimpleVO> getAdminMerchants(Long adminUserId) {
        log.info("获取管理员的民宿列表: adminUserId={}", adminUserId);
        
        // 查询该管理员的所有已审核通过的民宿
        LambdaQueryWrapper<MerchantInfo> queryWrapper = new LambdaQueryWrapper<MerchantInfo>()
                .eq(MerchantInfo::getAdminUserId, adminUserId)
                .eq(MerchantInfo::getAuditStatus, 1) // 已审核通过
                .eq(MerchantInfo::getStatus, 1) // 已启用
                .eq(MerchantInfo::getDeleted, 0); // 未删除
        
        List<MerchantInfo> merchants = merchantInfoMapper.selectList(queryWrapper);
        
        // 转换为SimpleVO
        return merchants.stream().map(merchant -> {
            MerchantSimpleVO vo = new MerchantSimpleVO();
            vo.setId(merchant.getId());
            vo.setMerchantName(merchant.getMerchantName());
            vo.setMerchantCode(merchant.getMerchantCode());
            vo.setAddress(merchant.getAddress());
            vo.setRoomCount(merchant.getRoomCount());
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public MerchantInfo getMerchantByAdminUserId(Long adminUserId) {
        log.info("根据管理员用户ID获取商户信息: adminUserId={}", adminUserId);
        
        LambdaQueryWrapper<MerchantInfo> queryWrapper = new LambdaQueryWrapper<MerchantInfo>()
                .eq(MerchantInfo::getAdminUserId, adminUserId)
                .eq(MerchantInfo::getDeleted, 0)
                .last("LIMIT 1");
        
        return merchantInfoMapper.selectOne(queryWrapper);
    }
    
    @Override
    public SysUser getUserByUsername(String username) {
        log.info("根据用户名获取用户信息: username={}", username);
        
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .and(wrapper -> wrapper
                    .eq(SysUser::getUsername, username)
                    .or()
                    .eq(SysUser::getEmail, username)
                    .or()
                    .eq(SysUser::getPhone, username)
                )
                .eq(SysUser::getDeleted, 0)
                .last("LIMIT 1");
        
        return userMapper.selectOne(queryWrapper);
    }
}
