package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.wallet.entity.WalletBrand;

import java.util.List;

@Mapper
public interface WalletBrandMapper {
    @Select("SELECT * FROM wallet_brand")
    List<WalletBrand> findAllWalletBrands();

    @Select("SELECT * FROM wallet_brand WHERE wallet_id = #{walletId}")
    WalletBrand findWalletBrandById(Long walletId);
}
