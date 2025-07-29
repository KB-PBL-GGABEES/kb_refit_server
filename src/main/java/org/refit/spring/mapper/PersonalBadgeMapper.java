package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.refit.spring.wallet.entity.PersonalBadge;

import java.util.List;

@Mapper
public interface PersonalBadgeMapper {
    @Select("SELECT badge_id FROM personal_badge WHERE user_id = #{userId}")
    List<Long> findBadgeIdsByUserId(Long userId);

    @Select("SELECT * FROM personal_badge WHERE user_id = #{userId} AND is_worn = true")
    List<PersonalBadge> findWornBadgesByUserId(Long userId);

    @Select("SELECT COUNT(*) > 0 FROM personal_badge WHERE user_id = #{userId} AND badge_id = #{badgeId} ")
    boolean existsByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Select("SELECT * FROM personal_badge WHERE user_id = #{userId} AND badge_id = #{badgeId}")
    PersonalBadge findByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Update("UPDATE personal_badge SET is_worn = #{isWorn}, updated_at = NOW() WHERE user_id = #{userId} AND badge_id = #{badgeId}")
    void updateIsWorn(@Param("userId") Long userId, @Param("badgeId") Long badgeId, @Param("isWorn") boolean isWorn);

    @Select("SELECT * FROM personal_badge WHERE user_id = #{userId} AND badge_id = #{badgeId}")
    PersonalBadge findPersonalBadge(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Update("UPDATE personal_badge SET is_worn = #{isWorn} WHERE user_id = #{userId} AND badge_id = #{badgeId}")
    void updateBadgeWornStatus(@Param("userId") Long userId,
                               @Param("badgeId") Long badgeId,
                               @Param("isWorn") boolean isWorn);
}
