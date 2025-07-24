package org.refit.spring.reward.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.RewardMapper;
import org.refit.spring.reward.dto.RewardListDto;
import org.refit.spring.reward.entity.Reward;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {
    private final RewardMapper rewardMapper;

    @Transactional(readOnly = true)
    public RewardListDto getList(Long userId, Long cursorId) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;
        List<Reward> rewards = rewardMapper.getList(cursorId);
        Long nextCursorId = rewards.size() < 20 ? null : rewards.get(rewards.size() - 1).getRewardId();
        return RewardListDto.from(userId, rewards, nextCursorId);



    }
 }
