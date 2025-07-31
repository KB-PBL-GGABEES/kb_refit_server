package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.refit.spring.wallet.entity.PersonalWalletBrand;

import java.util.List;

@Mapper
public interface PersonalWalletBrandMapper {
    @Select("SELECT wallet_id FROM personal_wallet_brand WHERE user_id = #{userId}")
    List<Long> findOwnedWalletIdsByUserId(Long userId);

    @Select("SELECT * FROM personal_wallet_brand WHERE user_id = #{userId} AND wallet_id = #{walletId}")
    PersonalWalletBrand findByUserIdAndWalletId(@Param("userId") Long userId,
                                                @Param("walletId") Long walletId);

    // 기존 착용 지갑 해제
    @Update("UPDATE personal_wallet_brand SET is_mounted = FALSE WHERE user_id = #{userId} AND is_mounted = TRUE")
    void unmountCurrentWallet(Long userId);

    // 새 지갑 착용
    @Update("UPDATE personal_wallet_brand SET is_mounted = TRUE WHERE user_id = #{userId} AND wallet_id = #{walletId}")
    void mountNewWallet(@Param("userId") Long userId, @Param("walletId") Long walletId);

    // 현재 내가 착용하고 있는 지갑 조회
    @Select("SELECT * FROM personal_wallet_brand WHERE user_id = #{userId} AND is_mounted = true LIMIT 1")
    PersonalWalletBrand findMountedWalletByUserId(Long userId);
}
