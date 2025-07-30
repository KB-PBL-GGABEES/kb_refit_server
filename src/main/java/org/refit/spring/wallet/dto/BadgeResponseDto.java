package org.refit.spring.wallet.dto;

import lombok.*;
import org.refit.spring.wallet.entity.Badge;
import org.refit.spring.wallet.entity.BadgePreset;
import org.refit.spring.wallet.entity.BadgePresetDetail;
import org.refit.spring.wallet.entity.PersonalBadge;

import java.util.List;
import java.util.stream.Collectors;

import static io.swagger.models.properties.PropertyBuilder.build;

public class BadgeResponseDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class BadgeListDetailDto {
        private Long badgeId;
        private String badgeImage;
        private boolean isOwned;

        public static BadgeListDetailDto from(Badge badge, boolean isOwned) {
            return BadgeListDetailDto.builder()
                    .badgeId(badge.getBadgeId())
                    .badgeImage(badge.getBadgeImage())
                    .isOwned(isOwned)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class BadgeListDto {
        private List<BadgeListDetailDto> badgeList;

        public static BadgeListDto from(List<BadgeListDetailDto> badgeList) {
            return BadgeListDto.builder()
                    .badgeList(badgeList)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class BadgeDetailDto {
        private Long badgeId;
        private Long personalBadgeId;
        private String badgeImage;
        private String badgeTitle;
        private String badgeBenefit;
        private boolean isWorn;

        public static BadgeDetailDto from(Badge badge, PersonalBadge personalBadge) {
            return BadgeDetailDto.builder()
                    .badgeId(badge.getBadgeId())
                    .personalBadgeId(personalBadge.getPersonalBadgeId())
                    .badgeImage(badge.getBadgeImage())
                    .badgeTitle(badge.getBadgeTitle())
                    .badgeBenefit(badge.getBadgeBenefit())
                    .isWorn(personalBadge.isWorn())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class wornBadgeListAndBenefitDto {
        private List<BadgeDetailDto> myBadgeList;

        public static wornBadgeListAndBenefitDto from(List<BadgeDetailDto> myBadgeList) {
            return wornBadgeListAndBenefitDto.builder()
                    .myBadgeList(myBadgeList)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class specificBadgeDetailDto {
        private Long badgeId;
        private String badgeImage;
        private String badgeTitle;
        private String badgeCondition;
        private String badgeBenefit;
        private boolean isOwned;

        public static specificBadgeDetailDto from(Badge badge, boolean isOwned) {
            return specificBadgeDetailDto.builder()
                    .badgeId(badge.getBadgeId())
                    .badgeImage(badge.getBadgeImage())
                    .badgeTitle(badge.getBadgeTitle())
                    .badgeCondition(badge.getBadgeCondition())
                    .badgeBenefit(badge.getBadgeBenefit())
                    .isOwned(isOwned)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class ToggleWornBadgeDto {
        private Long userId;
        private Long badgeId;
        private boolean isWorn;

        public static ToggleWornBadgeDto from(PersonalBadge personalBadge) {
            return ToggleWornBadgeDto.builder()
                    .badgeId(personalBadge.getBadgeId())
                    .userId(personalBadge.getUserId())
                    .isWorn(personalBadge.isWorn())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class BadgePresetDto {
        private Long personalBadgeId;

        public static BadgePresetDto from(BadgePresetDetail badgePresetDetail) {
            return BadgePresetDto.builder()
                    .personalBadgeId(badgePresetDetail.getPersonalBadgeId())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class BadgePresetListDto {
        private Long presetId;
        private String presetName;
        private boolean isApplied;
        List<BadgePresetDto> badgePresetList;

        public static BadgePresetListDto from(BadgePreset badgePreset, List<BadgePresetDto> presetList) {
            return BadgePresetListDto.builder()
                    .presetId(badgePreset.getPresetId())
                    .presetName(badgePreset.getPresetName())
                    .isApplied(badgePreset.isApplied())
                    .badgePresetList(presetList)
                    .build();
        }
    }
}

