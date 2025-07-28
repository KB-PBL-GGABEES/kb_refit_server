package org.refit.spring.wallet.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalWalletBrand {
    private Long personalWalletBrandId;
    private boolean isMounted;
    private Date createdAt;
    private Long walletId;
    private Long userId;
}
