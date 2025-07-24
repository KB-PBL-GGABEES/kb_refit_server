package org.refit.spring.reward.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reward {
    private Long rewardId;
    private Long userId;
    private Long carbonPoint;
    private Long reward;
    private Date createdAt;
}
