package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.wallet.entity.PersonalBadge;

import java.util.List;

@Mapper
public interface PersonalBadgeMapper {
    @Select("SELECT badge_id FROM personal_badge WHERE user_id = #{userId}")
    List<Long> findBadgeIdsByUserId(Long userId);

    @Select("SELECT * FROM personal_badge WHERE user_id = #{userId} AND is_worn = true")
    List<PersonalBadge> findWornBadgesByUserId(Long userId);
}
