package org.refit.spring.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalBadge {
    private Long personalBadgeId;
    private boolean isWorn;
    private Date createdAt;
    private Date updatedAt;
    private Long badgeId;
    private Long userId;
}
