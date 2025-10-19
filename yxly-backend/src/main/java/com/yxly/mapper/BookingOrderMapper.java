package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.BookingOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * 预订订单 Mapper接口
 * 
 * @author yxly
 * @since 2024-01-01
 */
@Mapper
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

    /**
     * 检查房间在指定时间段内是否有冲突的有效订单
     * 有效订单：待确认(1)、已确认(2)、已入住(3)
     * 时间段冲突判断：新订单入住日期 < 现有订单退房日期 AND 新订单退房日期 > 现有订单入住日期
     * 
     * @param roomId 房间ID
     * @param checkInDate 新订单入住日期
     * @param checkOutDate 新订单退房日期
     * @return 冲突的订单数量
     */
    @Select("SELECT COUNT(*) FROM booking_order " +
            "WHERE room_id = #{roomId} " +
            "AND booking_status IN (1, 2, 3) " +
            "AND check_in_date < #{checkOutDate} " +
            "AND check_out_date > #{checkInDate} " +
            "AND deleted = 0")
    int countConflictOrders(@Param("roomId") Long roomId,
                           @Param("checkInDate") LocalDate checkInDate,
                           @Param("checkOutDate") LocalDate checkOutDate);
}
