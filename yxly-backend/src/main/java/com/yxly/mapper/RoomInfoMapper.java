package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.entity.RoomInfo;
import com.yxly.dto.response.RoomResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 房间信息Mapper接口
 */
@Mapper
public interface RoomInfoMapper extends BaseMapper<RoomInfo> {

    /**
     * 分页查询房间信息（包含房型名称和民宿位置名称）
     *
     * @param page 分页参数
     * @param locationId 民宿位置ID
     * @param roomNumber 房间号
     * @param roomTypeId 房型ID
     * @param status 房间状态
     * @param floorNumber 楼层
     * @return 房间信息分页结果
     */
    @Select("<script>" +
            "SELECT r.*, rt.type_name as room_type_name, l.name as location_name " +
            "FROM room_info r " +
            "LEFT JOIN room_type rt ON r.room_type_id = rt.id " +
            "LEFT JOIN location_info l ON r.location_id = l.id " +
            "WHERE r.deleted = 0 " +
            "<if test='locationId != null'>" +
            "AND r.location_id = #{locationId} " +
            "</if>" +
            "<if test='roomNumber != null and roomNumber != \"\"'>" +
            "AND r.room_number LIKE CONCAT('%', #{roomNumber}, '%') " +
            "</if>" +
            "<if test='roomTypeId != null'>" +
            "AND r.room_type_id = #{roomTypeId} " +
            "</if>" +
            "<if test='status != null'>" +
            "AND r.status = #{status} " +
            "</if>" +
            "<if test='floorNumber != null'>" +
            "AND r.floor_number = #{floorNumber} " +
            "</if>" +
            "ORDER BY r.room_number ASC" +
            "</script>")
    IPage<RoomResponse> selectRoomPage(Page<RoomResponse> page,
                                       @Param("locationId") Long locationId,
                                       @Param("roomNumber") String roomNumber,
                                       @Param("roomTypeId") Long roomTypeId,
                                       @Param("status") Integer status,
                                       @Param("floorNumber") Integer floorNumber);

    /**
     * 根据房间号查询房间信息
     *
     * @param roomNumber 房间号
     * @return 房间信息
     */
    @Select("SELECT * FROM room_info WHERE room_number = #{roomNumber} AND deleted = 0")
    RoomInfo selectByRoomNumber(@Param("roomNumber") String roomNumber);

    /**
     * 根据房间ID查询房间详情（包含房型信息和民宿位置信息）
     *
     * @param id 房间ID
     * @return 房间详情
     */
    @Select("SELECT r.*, rt.type_name as room_type_name, l.name as location_name " +
            "FROM room_info r " +
            "LEFT JOIN room_type rt ON r.room_type_id = rt.id " +
            "LEFT JOIN location_info l ON r.location_id = l.id " +
            "WHERE r.id = #{id} AND r.deleted = 0")
    RoomResponse selectRoomDetail(@Param("id") Long id);
}