package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间类型实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room_type")
public class RoomType {

    /**
     * 房型ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房型名称
     */
    @TableField("type_name")
    private String typeName;

    /**
     * 房型编码
     */
    @TableField("type_code")
    private String typeCode;

    /**
     * 房型描述
     */
    @TableField("description")
    private String description;

    /**
     * 基础价格
     */
    @TableField("base_price")
    private BigDecimal basePrice;

    /**
     * 最大入住人数
     */
    @TableField("max_guests")
    private Integer maxGuests;

    /**
     * 床型
     */
    @TableField("bed_type")
    private String bedType;

    /**
     * 面积(平方米)
     */
    @TableField("area")
    private BigDecimal area;

    /**
     * 标准设施(JSON格式)
     */
    @TableField("facilities")
    private String facilities;

    /**
     * 房型图片(JSON格式)
     */
    @TableField("images")
    private String images;

    /**
     * 排序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 状态(0:禁用 1:启用)
     */
    @TableField("status")
    private Integer status;

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