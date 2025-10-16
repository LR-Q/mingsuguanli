package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.WithdrawRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提现记录Mapper接口
 */
@Mapper
public interface WithdrawRecordMapper extends BaseMapper<WithdrawRecord> {
}
