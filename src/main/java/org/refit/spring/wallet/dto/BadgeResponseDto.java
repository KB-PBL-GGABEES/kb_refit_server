package org.refit.spring.wallet.dto;

import lombok.*;
import org.refit.spring.wallet.entity.Badge;

import java.util.List;
import java.util.stream.Collectors;

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
}
