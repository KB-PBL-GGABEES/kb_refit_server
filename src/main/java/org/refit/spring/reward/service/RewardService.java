package org.refit.spring.reward.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.auth.entity.User;
import org.refit.spring.common.exception.DataMismatchException;
import org.refit.spring.mapper.PersonalBadgeMapper;
import org.refit.spring.mapper.RewardMapper;
import org.refit.spring.mapper.UserMapper;
import org.refit.spring.receipt.dto.ReceiptListCursorDto;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.receipt.enums.ReceiptType;
import org.refit.spring.receipt.service.ReceiptService;
import org.refit.spring.reward.dto.*;
import org.refit.spring.reward.entity.Reward;
import org.refit.spring.reward.enums.RewardType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RewardService {

    private static final double REWARD_RATE = 0.05;
    private final RewardMapper rewardMapper;
    private final UserMapper userMapper;
    private final PersonalBadgeMapper personalBadgeMapper;

    public void validateRequiredFields(Map<String, Object> fields) {
        List<String> missing = new ArrayList<>();

        for (Map.Entry<String, Object> entry: fields.entrySet()) {
            Object value = entry.getValue();
            if (value == null) missing.add(entry.getKey());
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("다음 필수 항목이 누락되었거나 비어 있습니다: " + String.join(", ", missing));
        }
    }

    public Reward create(Long carbon, Long totalPrice, Long userId, Long receiptId) {
        Reward reward = new Reward();
        reward.setCarbonPoint(carbon);
        if (personalBadgeMapper.checkIsWorn(userId, receiptId)) {
            reward.setReward((long) (totalPrice * REWARD_RATE));
        }
        else reward.setReward(0L);
        reward.setCreatedAt(new Date());
        reward.setUserId(userId);
        rewardMapper.createCarbon(reward);
        rewardMapper.createReward(reward);
        return reward;
    }

    @Transactional(readOnly = true)
    public RewardListCursorDto getList(Long userId, RewardListRequestDto rewardListRequestDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        long paginationSize = (rewardListRequestDto.getSize() != null && rewardListRequestDto.getSize() > 0) ? rewardListRequestDto.getSize() : 20;

        params.put("size", paginationSize);
        if (rewardListRequestDto.getSort() == null) rewardListRequestDto.setSort(ReceiptSort.LATEST);

        if (rewardListRequestDto.getCursorId() == null) {
            rewardListRequestDto.setCursorId((rewardListRequestDto.getSort() == ReceiptSort.OLDEST) ? 0L : Long.MAX_VALUE);
        }

        if (rewardListRequestDto.getPeriod() == null) {
            params.put("startDate", rewardListRequestDto.getStartDate());
            params.put("endDate", rewardListRequestDto.getEndDate());
        } else {
            params.put("period",rewardListRequestDto.getPeriod());
        }

        if (rewardListRequestDto.getType() == null) rewardListRequestDto.setType(RewardType.ALL);

        params.put("cursorId", rewardListRequestDto.getCursorId());
        params.put("sort", rewardListRequestDto.getSort());
        params.put("type", rewardListRequestDto.getType());

        validateRequiredFields(params);

        List<Reward> list = rewardMapper.getList(params);

        Long nextCursorId = (list.size() < paginationSize) ? null : list.get(list.size() - 1).getRewardId();
        return RewardListCursorDto.from(list, nextCursorId);
    }

    @Transactional(readOnly = true)
    public RewardSummaryDto getTotal(Long userId) {
        User user = userMapper.findByUserId(userId);
        RewardSummaryDto dto = new RewardSummaryDto();
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

    @Transactional
    public RewardWalletResponseDto purchaseWallet(Long userId, RewardWalletRequestDto dto) {
        if (rewardMapper.checkPossess(userId, dto.getWalletId())) {
            throw new IllegalArgumentException("이미 보유 중인 지갑입니다.");
        }
        RewardWalletResponseDto responseDto = new RewardWalletResponseDto();
        responseDto.setUserId(userId);
        responseDto.setWalletId(dto.getWalletId());
        Long cost = rewardMapper.getCost(dto.getWalletId());
        responseDto.setWalletCost(cost);
        User user = userMapper.findByUserId(userId);
        Long userPoint = user.getTotalStarPoint();
        if (userPoint < cost) {
            throw new IllegalArgumentException("보유 포인트가 부족합니다.");
        }
        responseDto.setTotalStarPoint(userPoint - cost);
        rewardMapper.updateStarPoint(userId, userPoint - cost);
        rewardMapper.createPersonal(userId, dto.getWalletId());

        return responseDto;
    }
 }
