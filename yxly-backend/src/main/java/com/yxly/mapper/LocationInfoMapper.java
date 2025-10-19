package com.yxly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yxly.entity.LocationInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 位置信息Mapper接口
 */
@Mapper
public interface LocationInfoMapper extends BaseMapper<LocationInfo> {

    /**
     * 查询启用的位置信息（用于用户端展示）
     *
     * @return 启用的位置信息
     */
    @Select("SELECT * FROM location_info WHERE is_active = 1 AND deleted = 0 ORDER BY update_time DESC LIMIT 1")
    LocationInfo selectActiveLocation();

    /**
     * 根据ID查询位置信息
     *
     * @param id 位置ID
     * @return 位置信息
     */
    @Select("SELECT * FROM location_info WHERE id = #{id} AND deleted = 0")
    LocationInfo selectByIdWithoutDeleted(@Param("id") Long id);
}
