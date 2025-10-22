package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏 Mapper 接口
 * 
 * @author yxly
 * @since 2024-10-20
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {
    
    /**
     * 查询用户的收藏房间列表（包含房间详情）
     * 
     * @param userId 用户ID
     * @return 收藏房间列表
     */
    @Select("SELECT uf.id AS id, " +
            "uf.user_id AS userId, " +
            "uf.room_id AS roomId, " +
            "uf.create_time AS createTime, " +
            "r.room_number AS roomNumber, " +
            "r.base_price AS basePrice, " +
            "r.current_price AS currentPrice, " +
            "r.area AS area, " +
            "r.bed_type AS bedType, " +
            "r.max_guests AS maxGuests, " +
            "r.description AS description, " +
            "r.images AS images, " +
            "r.facilities AS facilities, " +
            "r.status AS status, " +
            "rt.type_name AS roomTypeName " +
            "FROM user_favorite uf " +
            "LEFT JOIN room_info r ON uf.room_id = r.id AND r.deleted = 0 " +
            "LEFT JOIN room_type rt ON r.room_type_id = rt.id AND rt.deleted = 0 " +
            "WHERE uf.user_id = #{userId} AND uf.deleted = 0 " +
            "ORDER BY uf.create_time DESC")
    List<Map<String, Object>> selectFavoriteRoomsByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否收藏了某个房间
     * 
     * @param userId 用户ID
     * @param roomId 房间ID
     * @return 收藏记录数量
     */
    @Select("SELECT COUNT(*) FROM user_favorite " +
            "WHERE user_id = #{userId} AND room_id = #{roomId} AND deleted = 0")
    int checkFavorite(@Param("userId") Long userId, @Param("roomId") Long roomId);
}
