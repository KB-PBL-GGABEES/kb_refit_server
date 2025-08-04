package org.refit.spring.wallet.dto;

import lombok.*;
import org.refit.spring.auth.entity.User;
import org.refit.spring.wallet.entity.PersonalBadge;
import org.refit.spring.wallet.entity.PersonalWalletBrand;
import org.refit.spring.wallet.entity.WalletBrand;

import java.util.List;

public class WalletResponseDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class WalletBrandDto {
        private Long walletId;
        private String walletImage;
        private Long walletCost;
        private boolean isOwned;

        public static WalletBrandDto from(WalletBrand walletBrand, boolean isOwned) {
            return WalletBrandDto.builder()
                    .walletId(walletBrand.getWalletId())
                    .walletImage(walletBrand.getBrandImage())
                    .walletCost(walletBrand.getWalletCost())
                    .isOwned(isOwned)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class WalletBrandListDto {
        private Long userId;
        private Long starPoint;
        private List<WalletBrandDetailDto> walletBrandDtoList;

        public static WalletBrandListDto from(List<WalletBrandDetailDto> brandList, User user) {
            return WalletBrandListDto.builder()
                    .userId(user.getUserId())
                    .starPoint(user.getTotalStarPoint())
                    .walletBrandDtoList(brandList)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class WalletBrandDetailDto {
        private Long walletId;
        private Long totalStarPoint;
        private String brandName;
        private String brandImage;
        private Long walletCost;
        private boolean isOwned; // 지갑 보유 여부
        private boolean isMounted; // 착용여부

        public static WalletBrandDetailDto from(WalletBrand brand, User user, PersonalWalletBrand personalWalletBrand, boolean isOwned) {
            return WalletBrandDetailDto.builder()
                    .walletId(brand.getWalletId())
                    .totalStarPoint(user.getTotalStarPoint())
                    .brandName(brand.getBrandName())
                    .brandImage(brand.getBrandImage())
                    .walletCost(brand.getWalletCost())
                    .isOwned(isOwned)
                    .isMounted(personalWalletBrand.isMounted())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class ToggleMountedWalletDto {
        private Long userId;
        private Long walletId;
        private boolean isMounted;

        public static WalletResponseDto.ToggleMountedWalletDto from(PersonalWalletBrand personalWalletBrand) {
            return ToggleMountedWalletDto.builder()
                    .userId(personalWalletBrand.getUserId())
                    .walletId(personalWalletBrand.getWalletId())
                    .isMounted(personalWalletBrand.isMounted())
                    .build();
        }
    }

}
