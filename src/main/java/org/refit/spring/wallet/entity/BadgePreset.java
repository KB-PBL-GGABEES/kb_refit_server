package org.refit.spring.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgePreset {
    private Long badgePresetId;
    private String presetName;
    private boolean isApplied;
    private Long userId;
}
