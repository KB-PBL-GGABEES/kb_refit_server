package org.refit.spring.reward.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class RewardWalletResponseDto {
    @ApiModelProperty(value = "유저 아이디", example = "1")
    private Long userId;
    @ApiModelProperty(value = "지갑 아이디", example = "2")
    private Long walletId;
    @ApiModelProperty(value = "지갑 가격", example = "1400")
    private Long walletCost;
    @ApiModelProperty(value = "남은 스타 포인트", example = "5000")
    private Long totalStarPoint;
}
