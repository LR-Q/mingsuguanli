package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.dto.response.RechargeRecordResponse;
import com.yxly.entity.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 充值记录Mapper接口
 */
@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {
    
    /**
     * 分页查询充值记录（管理员）
     */
    IPage<RechargeRecordResponse> selectRechargeRecordPage(
            Page<RechargeRecordResponse> page,
            @Param("username") String username,
            @Param("status") Integer status,
            @Param("paymentMethod") String paymentMethod
    );
    
    /**
     * 分页查询用户充值记录
     */
    IPage<RechargeRecordResponse> selectUserRechargeRecordPage(
            Page<RechargeRecordResponse> page,
            @Param("userId") Long userId
    );
}







