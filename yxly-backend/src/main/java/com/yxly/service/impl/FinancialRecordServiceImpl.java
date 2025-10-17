package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.entity.FinancialRecord;
import com.yxly.mapper.FinancialRecordMapper;
import com.yxly.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 财务记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {
    
    private final FinancialRecordMapper financialRecordMapper;
    
    @Override
    public IPage<FinancialRecord> getUserFinancialRecords(Long userId, Long current, Long size, String category) {
        log.info("获取用户财务记录: userId={}, current={}, size={}, category={}", userId, current, size, category);
        
        Page<FinancialRecord> page = new Page<>(current, size);
        LambdaQueryWrapper<FinancialRecord> wrapper = new LambdaQueryWrapper<>();
        
        // 查询该用户的记录
        wrapper.eq(FinancialRecord::getOperatorId, userId);
        
        // 只查询有效的记录
        wrapper.eq(FinancialRecord::getStatus, 1);
        
        // 按分类筛选（如果提供）
        if (category != null && !category.isEmpty()) {
            wrapper.eq(FinancialRecord::getCategory, category);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(FinancialRecord::getCreateTime);
        
        IPage<FinancialRecord> result = financialRecordMapper.selectPage(page, wrapper);
        log.info("获取用户财务记录成功，共{}条", result.getTotal());
        
        return result;
    }
}
