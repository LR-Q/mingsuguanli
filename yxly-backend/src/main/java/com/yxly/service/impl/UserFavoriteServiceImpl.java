package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxly.entity.UserFavorite;
import com.yxly.mapper.UserFavoriteMapper;
import com.yxly.service.UserFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户收藏 Service 实现
 * 
 * @author yxly
 * @since 2024-10-20
 */
@Service
public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> 
        implements UserFavoriteService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFavorite(Long userId, Long roomId) {
        // 检查是否已经收藏
        if (checkFavorite(userId, roomId)) {
            return true; // 已经收藏，直接返回成功
        }
        
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setRoomId(roomId);
        
        return save(favorite);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFavorite(Long userId, Long roomId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
               .eq(UserFavorite::getRoomId, roomId);
        
        return remove(wrapper);
    }
    
    @Override
    public boolean checkFavorite(Long userId, Long roomId) {
        return baseMapper.checkFavorite(userId, roomId) > 0;
    }
    
    @Override
    public List<Map<String, Object>> getFavoriteList(Long userId) {
        return baseMapper.selectFavoriteRoomsByUserId(userId);
    }
    
    @Override
    public Map<Long, Boolean> checkBatchFavorites(Long userId, List<Long> roomIds) {
        Map<Long, Boolean> resultMap = new HashMap<>();
        
        if (roomIds == null || roomIds.isEmpty()) {
            return resultMap;
        }
        
        for (Long roomId : roomIds) {
            resultMap.put(roomId, checkFavorite(userId, roomId));
        }
        
        return resultMap;
    }
}
