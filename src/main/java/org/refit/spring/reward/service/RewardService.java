package org.refit.spring.reward.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.common.exception.DataMismatchException;
import org.refit.spring.mapper.RewardMapper;
import org.refit.spring.mapper.UserMapper;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.dto.RewardResponseDto;
import org.refit.spring.reward.entity.Reward;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final double REWARD_RATE = 0.05;
    private final RewardMapper rewardMapper;
    private final UserMapper userMapper;

    public Reward create(Long carbon, Long totalPrice, Long userId) {
        Reward reward = new Reward();
        reward.setCarbonPoint(carbon);
        reward.setReward((long) Math.floor(totalPrice * REWARD_RATE));
        reward.setCreatedAt(new Date());
        reward.setUserId(userId);
        rewardMapper.create(reward);
        return reward;
    }

    @Transactional(readOnly = true)
    public RewardListDto getList(Long userId, Long cursorId) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;
        List<Reward> rewards = rewardMapper.getList(userId, cursorId);
        Long nextCursorId = rewards.size() < 20 ? null : rewards.get(rewards.size() - 1).getRewardId();
        return RewardListDto.from(userId, rewards, nextCursorId);
    }

    @Transactional(readOnly = true)
    public RewardResponseDto getTotal(Long userId) {
        User user = userMapper.findByUserId(userId);
        RewardResponseDto dto = new RewardResponseDto();
        Long totalCashback = rewardMapper.getTotalCashback(userId);
        Long totalCarbon = rewardMapper.getTotalCarbon(userId);
        dto.setTotalCashback(totalCashback);
        if (!Objects.equals(user.getTotalCarbonPoint(), totalCarbon)) {
            throw new DataMismatchException("총 탄소 포인트의 합계가 유저의 탄소 포인트와 일치하지 않습니다. (user.totalCarbonPoint = " + user.getTotalCarbonPoint() + ", Carbon 합계 = " + totalCarbon + ")");
        }
        else {
            dto.setTotalCarbonPoint(totalCarbon);
        }
        dto.setTotalStarPoint(user.getTotalStarPoint());
        dto.setCategory(rewardMapper.getCategory(userId));
        return dto;
    }
 }
