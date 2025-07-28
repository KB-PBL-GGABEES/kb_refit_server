package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.wallet.entity.WalletBrand;

import java.util.List;

@Mapper
public interface WalletBrandMapper {
    @Select("SELECT * FROM wallet_brand")
    List<WalletBrand> findAllWalletBrands();

    @Select("SELECT wallet_id FROM personal_wallet_brand WHERE user_id = #{userId}")
    List<Long> findOwnedWalletIdsByUserId(Long userId);
}
