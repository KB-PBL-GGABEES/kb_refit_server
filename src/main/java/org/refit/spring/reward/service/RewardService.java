package org.refit.spring.reward.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.RewardMapper;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.dto.RewardResponseDto;
import org.refit.spring.reward.entity.Reward;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final double REWARD_RATE = 0.05;
    private final RewardMapper rewardMapper;

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
    public RewardResponseDto getTotalCashback(Long userId) {
        RewardResponseDto dto = new RewardResponseDto();


        return dto;
    }
 }
