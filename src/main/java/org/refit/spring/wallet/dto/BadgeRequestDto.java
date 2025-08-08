package org.refit.spring.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BadgeRequestDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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
