package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
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

    @Update("UPDATE personal_badge SET is_worn = #{isWorn} WHERE user_id = #{userId} AND badge_id = #{badgeId}")
    void updateBadgeWornStatus(@Param("userId") Long userId,
                               @Param("badgeId") Long badgeId,
                               @Param("isWorn") boolean isWorn);

    @Update("UPDATE personal_badge SET is_worn = false WHERE user_id = #{userId}")
    void unwearAllBadgesByUserId(Long userId);

    @Update("UPDATE personal_badge SET is_worn = #{isWorn} WHERE personal_badge_id = #{personalBadgeId}")
    void updateIsWornByPersonalBadgeId(@Param("personalBadgeId") Long personalBadgeId, @Param("isWorn") boolean isWorn);

    @Select("SELECT badge_id FROM personal_badge WHERE personal_badge_id = #{personalBadgeId}")
    Long findBadgeIdByPersonalBadgeId(Long personalBadgeId);

    @Select("SELECT badge_condition FROM badge WHERE badge_id = #{badgeId}")
    String getCondition(@Param("badgeId") Long badgeId);

    @Insert("INSERT personal_badge (is_worn, created_at, updated_at, badge_id, user_id) VALUES (0, now(), now(), #{badgeId}, #{userId})")
    void insertBadge(@Param("badgeId") Long badgeId, @Param("userId") Long userId);

    @Select("SELECT * FROM personal_badge WHERE personal_badge_id = #{personalBadgeId}")
    PersonalBadge findById(Long personalBadgeId);
}

