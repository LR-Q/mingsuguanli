package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yxly.dto.request.RoomCreateRequest;
import com.yxly.dto.request.RoomUpdateRequest;
import com.yxly.dto.response.RoomResponse;
import com.yxly.dto.response.RoomTypeResponse;

import java.util.List;

/**
 * 房间管理服务接口
 */
public interface RoomService {

    /**
     * 分页查询房间列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param locationId 民宿位置ID
     * @param roomNumber 房间号
     * @param roomTypeId 房型ID
     * @param status 房间状态
     * @param floorNumber 楼层
     * @return 房间分页结果
     */
    IPage<RoomResponse> getRoomPage(Long current, Long size, Long locationId, String roomNumber, 
                                   Long roomTypeId, Integer status, Integer floorNumber);

    /**
     * 根据ID查询房间详情
     *
     * @param id 房间ID
     * @return 房间详情
     */
    RoomResponse getRoomById(Long id);

    /**
     * 创建房间
     *
     * @param request 创建房间请求
     * @return 房间ID
     */
    Long createRoom(RoomCreateRequest request);

    /**
     * 更新房间
     *
     * @param request 更新房间请求
     */
    void updateRoom(RoomUpdateRequest request);

    /**
     * 删除房间
     *
     * @param id 房间ID
     */
    void deleteRoom(Long id);

    /**
     * 获取所有启用的房型列表
     *
     * @return 房型列表
     */
    List<RoomTypeResponse> getEnabledRoomTypes();

    /**
     * 更新房间状态
     *
     * @param id 房间ID
     * @param status 新状态
     */
    void updateRoomStatus(Long id, Integer status);

    /**
     * 获取用户端可用房间列表
     *
     * @param current 当前页
     * @param size 每页大小
     * @param locationId 民宿位置ID
     * @param roomTypeId 房型ID
     * @param maxGuests 最大入住人数
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 可用房间分页结果
     */
    IPage<RoomResponse> getAvailableRoomsForUser(Long current, Long size, Long locationId, Long roomTypeId, 
                                               Integer maxGuests, Double minPrice, Double maxPrice);

    /**
     * 获取所有房型列表（包括禁用的）
     *
     * @return 房型列表
     */
    List<RoomTypeResponse> getAllRoomTypes();
}


