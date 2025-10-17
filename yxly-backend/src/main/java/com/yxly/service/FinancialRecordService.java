package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.entity.FinancialRecord;

/**
 * 财务记录服务接口
 */
public interface FinancialRecordService {
    
    /**
     * 获取用户财务记录（分页）
     */
    IPage<FinancialRecord> getUserFinancialRecords(Long userId, Long current, Long size, String category);
}
