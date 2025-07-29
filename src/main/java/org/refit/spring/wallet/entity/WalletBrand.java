package org.refit.spring.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBrand {
    private Long walletId;
    private String brandImage;
    private String brandName;
    private Long walletCost;
}
