package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yxly.dto.request.RoomCreateRequest;
import com.yxly.dto.request.RoomUpdateRequest;
import com.yxly.dto.response.RoomResponse;
import com.yxly.dto.response.RoomTypeResponse;
import com.yxly.entity.RoomInfo;
import com.yxly.entity.RoomType;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.RoomInfoMapper;
import com.yxly.mapper.RoomTypeMapper;
import com.yxly.service.RoomService;
import com.yxly.common.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 房间管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomInfoMapper roomInfoMapper;
    private final RoomTypeMapper roomTypeMapper;

    // 房间状态映射
    private static final Map<Integer, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put(1, "可用");
        STATUS_MAP.put(2, "占用");
        STATUS_MAP.put(3, "维修");
        STATUS_MAP.put(4, "清洁");
        STATUS_MAP.put(5, "停用");
    }

    @Override
    public IPage<RoomResponse> getRoomPage(Long current, Long size, Long locationId, String roomNumber, 
                                          Long roomTypeId, Integer status, Integer floorNumber) {
        log.info("分页查询房间列表: current={}, size={}, locationId={}, roomNumber={}, roomTypeId={}, status={}, floorNumber={}", 
                current, size, locationId, roomNumber, roomTypeId, status, floorNumber);
        
        Page<RoomResponse> page = new Page<>(current, size);
        IPage<RoomResponse> result = roomInfoMapper.selectRoomPage(page, locationId, roomNumber, roomTypeId, status, floorNumber);
        
        // 设置状态名称
        result.getRecords().forEach(room -> {
            room.setStatusName(STATUS_MAP.get(room.getStatus()));
        });
        
        return result;
    }

    @Override
    public RoomResponse getRoomById(Long id) {
        log.info("查询房间详情: id={}", id);
        
        RoomResponse room = roomInfoMapper.selectRoomDetail(id);
        if (room == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房间不存在");
        }
        
        room.setStatusName(STATUS_MAP.get(room.getStatus()));
        return room;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoom(RoomCreateRequest request) {
        log.info("创建房间: roomNumber={}", request.getRoomNumber());
        
        // 检查房间号是否已存在
        RoomInfo existingRoom = roomInfoMapper.selectByRoomNumber(request.getRoomNumber());
        if (existingRoom != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "房间号已存在");
        }
        
        // 检查房型是否存在
        RoomType roomType = roomTypeMapper.selectById(request.getRoomTypeId());
        if (roomType == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房型不存在");
        }
        
        // 创建房间信息
        RoomInfo roomInfo = new RoomInfo();
        BeanUtils.copyProperties(request, roomInfo);
        roomInfo.setCreateTime(LocalDateTime.now());
        roomInfo.setUpdateTime(LocalDateTime.now());
        
        int result = roomInfoMapper.insert(roomInfo);
        if (result <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "创建房间失败");
        }
        
        log.info("房间创建成功: id={}, roomNumber={}", roomInfo.getId(), roomInfo.getRoomNumber());
        return roomInfo.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoom(RoomUpdateRequest request) {
        log.info("更新房间: id={}", request.getId());
        
        // 检查房间是否存在
        RoomInfo existingRoom = roomInfoMapper.selectById(request.getId());
        if (existingRoom == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房间不存在");
        }
        
        // 检查房型是否存在
        RoomType roomType = roomTypeMapper.selectById(request.getRoomTypeId());
        if (roomType == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房型不存在");
        }
        
        // 更新房间信息
        RoomInfo roomInfo = new RoomInfo();
        BeanUtils.copyProperties(request, roomInfo);
        roomInfo.setUpdateTime(LocalDateTime.now());
        
        int result = roomInfoMapper.updateById(roomInfo);
        if (result <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "更新房间失败");
        }
        
        log.info("房间更新成功: id={}", request.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long id) {
        log.info("删除房间: id={}", id);
        
        // 检查房间是否存在
        RoomInfo existingRoom = roomInfoMapper.selectById(id);
        if (existingRoom == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房间不存在");
        }
        
        // 检查房间是否可以删除（例如：是否有未完成的订单）
        // 这里可以添加业务逻辑检查
        
        // 逻辑删除房间
        int result = roomInfoMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "删除房间失败");
        }
        
        log.info("房间删除成功: id={}", id);
    }

    @Override
    public List<RoomTypeResponse> getEnabledRoomTypes() {
        log.info("查询启用的房型列表");
        
        List<RoomType> roomTypes = roomTypeMapper.selectEnabledRoomTypes();
        return roomTypes.stream().map(roomType -> {
            RoomTypeResponse response = new RoomTypeResponse();
            BeanUtils.copyProperties(roomType, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoomStatus(Long id, Integer status) {
        log.info("更新房间状态: id={}, status={}", id, status);
        
        // 检查房间是否存在
        RoomInfo existingRoom = roomInfoMapper.selectById(id);
        if (existingRoom == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "房间不存在");
        }
        
        // 更新房间状态
        RoomInfo roomInfo = new RoomInfo();
        roomInfo.setId(id);
        roomInfo.setStatus(status);
        roomInfo.setUpdateTime(LocalDateTime.now());
        
        int result = roomInfoMapper.updateById(roomInfo);
        if (result <= 0) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "更新房间状态失败");
        }
        
        log.info("房间状态更新成功: id={}, status={}", id, status);
    }

    @Override
    public IPage<RoomResponse> getAvailableRoomsForUser(Long current, Long size, Long locationId, Long roomTypeId, 
                                                      Integer maxGuests, Double minPrice, Double maxPrice) {
        log.info("用户查询可用房间: locationId={}, roomTypeId={}, maxGuests={}, minPrice={}, maxPrice={}", 
                locationId, roomTypeId, maxGuests, minPrice, maxPrice);
        
        Page<RoomResponse> page = new Page<>(current, size);
        
        // 执行查询 - 只查询可用状态的房间
        // selectRoomPage(page, locationId, roomNumber, roomTypeId, status, floorNumber)
        IPage<RoomResponse> result = roomInfoMapper.selectRoomPage(page, locationId, null, roomTypeId, 1, null);
        
        // 过滤结果（根据最大入住人数和价格）
        if (maxGuests != null || minPrice != null || maxPrice != null) {
            List<RoomResponse> filteredRecords = result.getRecords().stream()
                .filter(room -> {
                    if (maxGuests != null && room.getMaxGuests() < maxGuests) {
                        return false;
                    }
                    if (minPrice != null && room.getCurrentPrice() != null && room.getCurrentPrice().doubleValue() < minPrice) {
                        return false;
                    }
                    if (maxPrice != null && room.getCurrentPrice() != null && room.getCurrentPrice().doubleValue() > maxPrice) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
            
            // 更新记录和总数
            result.setRecords(filteredRecords);
            result.setTotal(filteredRecords.size());
            
            // 重新计算分页信息
            long pages = (filteredRecords.size() + size - 1) / size;
            result.setPages(pages);
        }
        
        // 处理响应数据
        result.getRecords().forEach(room -> {
            room.setStatusName(STATUS_MAP.get(room.getStatus()));
        });
        
        log.info("用户查询可用房间完成: total={}, records={}", result.getTotal(), result.getRecords().size());
        return result;
    }

    @Override
    public List<RoomTypeResponse> getAllRoomTypes() {
        log.info("查询所有房型列表");
        
        List<RoomType> roomTypes = roomTypeMapper.selectList(null);
        
        return roomTypes.stream().map(roomType -> {
            RoomTypeResponse response = new RoomTypeResponse();
            BeanUtils.copyProperties(roomType, response);
            return response;
        }).collect(Collectors.toList());
    }
}


