package org.refit.spring.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewardSummaryDto {
    private Long totalCashback;
    private String category;
    private Long totalCarbonPoint;
    private Long totalStarPoint;
}
