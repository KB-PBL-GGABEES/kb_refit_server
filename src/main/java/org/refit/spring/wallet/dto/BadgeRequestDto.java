package org.refit.spring.wallet.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.refit.spring.wallet.entity.BadgePreset;

public class BadgeRequestDto {
    @Getter
    @NoArgsConstructor
    public static class UpdateWornBadgeDto {
        private Long previousBadgeId;
        private Long updateBadgeId;

        public boolean hasBadgeToUnwear() {
            return previousBadgeId != null;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SaveBadgePresetDto {
        private String presetName;
    }
}
