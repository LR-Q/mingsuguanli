package com.yxly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxly.dto.request.ReviewCreateRequest;
import com.yxly.dto.response.RoomReviewResponse;
import com.yxly.entity.BookingOrder;
import com.yxly.entity.RoomReview;
import com.yxly.entity.SysUser;
import com.yxly.exception.BusinessException;
import com.yxly.mapper.BookingOrderMapper;
import com.yxly.mapper.RoomReviewMapper;
import com.yxly.mapper.SysUserMapper;
import com.yxly.service.RoomReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomReviewServiceImpl extends ServiceImpl<RoomReviewMapper, RoomReview> implements RoomReviewService {

    private final BookingOrderMapper bookingOrderMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReview(Long userId, ReviewCreateRequest request) {
        // 强制校验：订单必须存在、属于本人、房间一致、状态为已退房(4)
        BookingOrder order = bookingOrderMapper.selectById(request.getOrderId());
        if (order == null || !Objects.equals(order.getCustomerId(), userId) || !Objects.equals(order.getRoomId(), request.getRoomId())) {
            throw new BusinessException("无权为该订单评论");
        }
        if (order.getBookingStatus() == null || order.getBookingStatus() < 4) {
            throw new BusinessException("入住完成后方可评价");
        }

        boolean isAppend = Boolean.TRUE.equals(request.getAppend());

        // 一单一评 + 允许一次追评
        LambdaQueryWrapper<RoomReview> mainQuery = new LambdaQueryWrapper<>();
        mainQuery.eq(RoomReview::getOrderId, request.getOrderId())
                 .eq(RoomReview::getIsAppend, 0);
        RoomReview main = getOne(mainQuery, false);

        if (!isAppend) {
            if (main != null) {
                throw new BusinessException("该订单已评价");
            }
        } else {
            if (main == null) {
                throw new BusinessException("请先提交首评后再追评");
            }
            LambdaQueryWrapper<RoomReview> appendQuery = new LambdaQueryWrapper<>();
            appendQuery.eq(RoomReview::getOrderId, request.getOrderId())
                       .eq(RoomReview::getIsAppend, 1);
            if (count(appendQuery) > 0) {
                throw new BusinessException("该订单已追评");
            }
        }

        RoomReview review = new RoomReview();
        review.setRoomId(request.getRoomId());
        review.setOrderId(request.getOrderId());
        review.setUserId(userId);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setAnonymous(Boolean.TRUE.equals(request.getAnonymous()) ? 1 : 0);
        review.setIsAppend(isAppend ? 1 : 0);
        review.setParentId(isAppend && main != null ? main.getId() : null);

        save(review);
    }

    @Override
    public IPage<RoomReviewResponse> getRoomReviews(Long roomId, Long current, Long size) {
        Page<RoomReview> page = new Page<>(current, size);
        LambdaQueryWrapper<RoomReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoomReview::getRoomId, roomId)
               .orderByDesc(RoomReview::getCreateTime);

        IPage<RoomReview> reviewPage = this.baseMapper.selectPage(page, wrapper);

        // 映射为响应对象
        Page<RoomReviewResponse> resp = new Page<>(reviewPage.getCurrent(), reviewPage.getSize(), reviewPage.getTotal());
        resp.setRecords(reviewPage.getRecords().stream().map(r -> {
            RoomReviewResponse rr = new RoomReviewResponse();
            rr.setId(r.getId());
            rr.setRoomId(r.getRoomId());
            rr.setRating(r.getRating());
            rr.setContent(r.getContent());
            rr.setAnonymous(r.getAnonymous() != null && r.getAnonymous() == 1);
            rr.setCreateTime(r.getCreateTime());

            SysUser user = sysUserMapper.selectById(r.getUserId());
            if (user != null) {
                String name = user.getRealName();
                if (name == null || name.isEmpty()) {
                    name = user.getUsername();
                }
                if (rr.getAnonymous()) {
                    // 匿名时打码
                    if (name != null && name.length() > 1) {
                        name = name.charAt(0) + "**";
                    }
                }
                rr.setUserDisplayName(name);
                rr.setUserAvatar(user.getAvatar());
            }
            return rr;
        }).collect(Collectors.toList()));

        return resp;
    }
}

