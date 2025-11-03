package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 房间评论实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room_review")
@Schema(description = "房间评论")
public class RoomReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("room_id")
    private Long roomId;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("rating")
    private Integer rating;

    @TableField("content")
    private String content;

    @TableField("anonymous")
    private Integer anonymous;

    @TableField("is_append")
    private Integer isAppend; // 0: 首评 1: 追评

    @TableField("parent_id")
    private Long parentId; // 追评关联的首评ID

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}


