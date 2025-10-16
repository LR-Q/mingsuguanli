package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.RoomType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房间类型Mapper接口
 */
@Mapper
public interface RoomTypeMapper extends BaseMapper<RoomType> {

    /**
     * 查询启用的房型列表
     *
     * @return 房型列表
     */
    @Select("SELECT * FROM room_type WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<RoomType> selectEnabledRoomTypes();

    /**
     * 根据房型编码查询房型
     *
     * @param typeCode 房型编码
     * @return 房型信息
     */
    @Select("SELECT * FROM room_type WHERE type_code = #{typeCode} AND deleted = 0")
    RoomType selectByTypeCode(@Param("typeCode") String typeCode);
}