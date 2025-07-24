package org.refit.spring.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge {
    private Long badgeId;
    private String badgeImage;
    private String badgeTitle;
    private String badgeBenefit;
    private String badgeCondition;
    private Long categoryId;
}

