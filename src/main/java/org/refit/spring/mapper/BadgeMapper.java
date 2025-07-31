package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.wallet.entity.Badge;

import java.util.List;

@Mapper
public interface BadgeMapper {
    @Select("SELECT * FROM badge")
    List<Badge> findAll();

    @Select("SELECT * FROM badge WHERE badge_id = #{badgeId}")
    Badge findById(Long badgeId);
}
