package org.refit.spring.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgePresetDetail {
    private Long badgePresetDetailId;
    private Long presetId;
    private Long personalBadgeId;
}
