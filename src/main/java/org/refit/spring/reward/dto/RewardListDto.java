package org.refit.spring.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.refit.spring.receipt.dto.ReceiptListDto;
import org.refit.spring.reward.entity.Reward;

import java.util.List;

@Data
@AllArgsConstructor
public class RewardListDto {
    private Long userId;
    private List<Reward> rewardList;
    private Long nextCursorId;

    public static RewardListDto from(Long userId, List<Reward> rewards, Long nextCursorId) {
        return new RewardListDto(
                userId,
                rewards,
                nextCursorId
        );
    }
}
