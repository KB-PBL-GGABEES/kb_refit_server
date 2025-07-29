package org.refit.spring.reward.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletBrand {
    private Long walletId;
    private String brandImage;
    private String brandName;
    private Long walletCost;
    private String brandIntroduce;
}
