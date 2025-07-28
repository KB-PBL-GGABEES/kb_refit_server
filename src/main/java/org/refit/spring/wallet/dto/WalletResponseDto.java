package org.refit.spring.wallet.dto;

import lombok.*;
import org.refit.spring.auth.entity.User;
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
        private List<WalletBrandDto> walletBrandDtoList;

        public static WalletBrandListDto from(List<WalletBrandDto> brandList, User user) {
            return WalletBrandListDto.builder()
                    .userId(user.getUserId())
                    .starPoint(user.getTotalStarPoint())
                    .walletBrandDtoList(brandList)
                    .build();
        }

    }
}
