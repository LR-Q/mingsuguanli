package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.FinancialRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 财务记录Mapper接口
 */
@Mapper
public interface FinancialRecordMapper extends BaseMapper<FinancialRecord> {
}
