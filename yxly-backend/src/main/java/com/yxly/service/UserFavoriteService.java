package com.yxly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yxly.entity.UserFavorite;

import java.util.List;
import java.util.Map;

/**
 * 用户收藏 Service 接口
 * 
 * @author yxly
 * @since 2024-10-20
 */
public interface UserFavoriteService extends IService<UserFavorite> {
    
    /**
     * 添加收藏
     * 
     * @param userId 用户ID
     * @param roomId 房间ID
     * @return 是否添加成功
     */
    boolean addFavorite(Long userId, Long roomId);
    
    /**
     * 取消收藏
     * 
     * @param userId 用户ID
     * @param roomId 房间ID
     * @return 是否取消成功
     */
    boolean removeFavorite(Long userId, Long roomId);
    
    /**
     * 检查是否已收藏
     * 
     * @param userId 用户ID
     * @param roomId 房间ID
     * @return 是否已收藏
     */
    boolean checkFavorite(Long userId, Long roomId);
    
    /**
     * 获取用户的收藏列表
     * 
     * @param userId 用户ID
     * @return 收藏房间列表
     */
    List<Map<String, Object>> getFavoriteList(Long userId);
    
    /**
     * 批量检查房间是否已收藏
     * 
     * @param userId 用户ID
     * @param roomIds 房间ID列表
     * @return 房间ID和收藏状态的映射
     */
    Map<Long, Boolean> checkBatchFavorites(Long userId, List<Long> roomIds);
}
