package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.wallet.entity.BadgePreset;
import org.refit.spring.wallet.entity.BadgePresetDetail;

import java.util.List;

@Mapper
public interface BadgePresetMapper {

    //insert
    @Insert("INSERT INTO badge_preset_detail (preset_id, personal_badge_id) VALUES (#{presetId}, #{personalBadgeId})")
    void insertPresetDetail(BadgePresetDetail detail);

    //프리셋 저장
    @Insert("INSERT INTO badge_preset (preset_name, is_applied, user_id) VALUES (#{presetName}, #{isApplied}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "presetId") // 프리셋 ID 반환 받기
    void insertBadgePreset(BadgePreset badgePreset);

    //preset 조회
    @Select("SELECT * FROM badge_preset WHERE user_id = #{userId}")
    List<BadgePreset> findAllByUserId(Long userId);

    @Select("SELECT * FROM badge_preset_detail WHERE preset_id = #{presetId}")
    List<BadgePresetDetail> findAllByPresetId(Long presetId);

    @Delete("DELETE FROM badge_preset WHERE preset_id = #{presetId}")
    void deletePresetById(Long presetId);
}
