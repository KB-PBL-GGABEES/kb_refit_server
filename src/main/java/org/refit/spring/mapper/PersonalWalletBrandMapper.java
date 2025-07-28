package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.wallet.entity.PersonalWalletBrand;

import java.util.List;

@Mapper
public interface PersonalWalletBrandMapper {
    @Select("SELECT wallet_id FROM personal_wallet_brand WHERE user_id = #{userId}")
    List<Long> findOwnedWalletIdsByUserId(Long userId);

    @Select("SELECT * FROM personal_wallet_brand WHERE user_id = #{userId} AND wallet_id = #{walletId}")
    PersonalWalletBrand findByUserIdAndWalletId(@Param("userId") Long userId,
                                                @Param("walletId") Long walletId);
}
