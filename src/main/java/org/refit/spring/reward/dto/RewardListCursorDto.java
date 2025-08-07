package org.refit.spring.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.refit.spring.reward.entity.Reward;

import java.util.List;

@Data
@AllArgsConstructor
public class RewardListCursorDto {
    List<Reward> rewardList;
    private Long nextCursorId;

    public static RewardListCursorDto from(List<Reward> list, Long nextCursorId) {
        return new RewardListCursorDto(list, nextCursorId);
    }
}
