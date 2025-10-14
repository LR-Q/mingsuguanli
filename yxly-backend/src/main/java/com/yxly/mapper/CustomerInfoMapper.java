package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.CustomerInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户信息 Mapper接口
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Mapper
public interface CustomerInfoMapper extends BaseMapper<CustomerInfo> {

}
