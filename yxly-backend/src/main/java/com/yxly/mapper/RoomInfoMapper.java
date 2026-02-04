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

    /**
     * 推荐房源分页（仅可用状态）
     */
    @Select("SELECT r.*, rt.type_name as room_type_name, l.name as location_name " +
            "FROM room_info r " +
            "LEFT JOIN room_type rt ON r.room_type_id = rt.id " +
            "LEFT JOIN location_info l ON r.location_id = l.id " +
            "WHERE r.deleted = 0 AND r.status = 1 AND r.is_recommended = 1 " +
            "ORDER BY r.update_time DESC")
    IPage<RoomResponse> selectRecommendedRooms(Page<RoomResponse> page);

    /**
     * 获取所有推荐房源ID
     */
    @Select("SELECT id FROM room_info WHERE deleted = 0 AND is_recommended = 1 ORDER BY update_time DESC")
    java.util.List<Long> selectRecommendedIds();

    /**
     * 批量清空推荐
     */
    @org.apache.ibatis.annotations.Update("UPDATE room_info SET is_recommended = 0 WHERE deleted = 0")
    int clearAllRecommendations();

    /**
     * 批量设置推荐
     */
    @org.apache.ibatis.annotations.Update("<script>" +
            "UPDATE room_info SET is_recommended = 1, recommend_time = NOW() " +
            "WHERE deleted = 0 AND id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int setRecommendations(@Param("ids") java.util.List<Long> ids);

    /**
     * 根据多个位置ID分页查询房间信息（管理员隔离）
     */
    @Select("<script>" +
            "SELECT r.*, rt.type_name as room_type_name, l.name as location_name " +
            "FROM room_info r " +
            "LEFT JOIN room_type rt ON r.room_type_id = rt.id " +
            "LEFT JOIN location_info l ON r.location_id = l.id " +
            "WHERE r.deleted = 0 " +
            "<if test='locationIds != null and locationIds.size() > 0'>" +
            "AND r.location_id IN " +
            "<foreach collection='locationIds' item='lid' open='(' separator=',' close=')'>#{lid}</foreach> " +
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
    IPage<RoomResponse> selectRoomPageByLocations(Page<RoomResponse> page,
                                                  @Param("locationIds") java.util.List<Long> locationIds,
                                                  @Param("roomNumber") String roomNumber,
                                                  @Param("roomTypeId") Long roomTypeId,
                                                  @Param("status") Integer status,
                                                  @Param("floorNumber") Integer floorNumber);

    /**
     * 更新推荐状态（带推荐时间）
     */
    @org.apache.ibatis.annotations.Update("UPDATE room_info SET is_recommended = #{recommended}, recommend_time = CASE WHEN #{recommended} = 1 THEN NOW() ELSE recommend_time END, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateRecommendStatus(@Param("id") Long id, @Param("recommended") Integer recommended);

    /**
     * 更新推荐状态（不更新推荐时间，兼容缺少 recommend_time 字段的环境）
     */
    @org.apache.ibatis.annotations.Update("UPDATE room_info SET is_recommended = #{recommended}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updateRecommendStatusNoTime(@Param("id") Long id, @Param("recommended") Integer recommended);
}
