package org.refit.spring.reward.dto;

import lombok.Data;

@Data
public class RewardWalletResponseDto {
    private Long userId;
    private Long walletId;
    private Long walletCost;
    private Long totalStarPoint;
}
