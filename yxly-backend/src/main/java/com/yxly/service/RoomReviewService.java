package com.yxly.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yxly.dto.request.ReviewCreateRequest;
import com.yxly.dto.response.RoomReviewResponse;
import com.yxly.entity.RoomReview;

public interface RoomReviewService extends IService<RoomReview> {
    void submitReview(Long userId, ReviewCreateRequest request);

    IPage<RoomReviewResponse> getRoomReviews(Long roomId, Long current, Long size);
}


