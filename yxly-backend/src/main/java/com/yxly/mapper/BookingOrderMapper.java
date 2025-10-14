package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.BookingOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预订订单 Mapper接口
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Mapper
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

}
