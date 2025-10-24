package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room_info")
public class RoomInfo {

    /**
     * 房间ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 民宿位置ID（关联location_info表）
     * 用于区分房间属于哪个具体的民宿位置
     */
    @TableField("location_id")
    private Long locationId;

    /**
     * 房间号
     */
    @TableField("room_number")
    private String roomNumber;

    /**
     * 房型ID
     */
    @TableField("room_type_id")
    private Long roomTypeId;

    /**
     * 楼层
     */
    @TableField("floor_number")
    private Integer floorNumber;

    /**
     * 面积(平方米)
     */
    @TableField("area")
    private BigDecimal area;

    /**
     * 床型
     */
    @TableField("bed_type")
    private String bedType;

    /**
     * 最大入住人数
     */
    @TableField("max_guests")
    private Integer maxGuests;

    /**
     * 基础价格
     */
    @TableField("base_price")
    private BigDecimal basePrice;

    /**
     * 当前价格
     */
    @TableField("current_price")
    private BigDecimal currentPrice;

    /**
     * 房间状态(1:可用 2:占用 3:维修 4:清洁 5:停用)
     */
    @TableField("status")
    private Integer status;

    /**
     * 房间设施(JSON格式)
     */
    @TableField("facilities")
    private String facilities;

    /**
     * 房间描述
     */
    @TableField("description")
    private String description;

    /**
     * 房间图片(JSON格式)
     */
    @TableField("images")
    private String images;

    /**
     * WiFi密码
     */
    @TableField("wifi_password")
    private String wifiPassword;

    /**
     * 最后清洁时间
     */
    @TableField("last_clean_time")
    private LocalDateTime lastCleanTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}