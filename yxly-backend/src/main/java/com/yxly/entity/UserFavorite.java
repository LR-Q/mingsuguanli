package com.yxly.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 用户收藏实体
 * 
 * @author yxly
 * @since 2024-10-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_favorite")
@Schema(description = "用户收藏")
public class UserFavorite {
    
    @Schema(description = "收藏ID")
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;
    
    @Schema(description = "房间ID")
    @TableField("room_id")
    private Long roomId;
    
    @Schema(description = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @Schema(description = "逻辑删除标记")
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
