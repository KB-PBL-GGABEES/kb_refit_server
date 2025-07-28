package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.reward.entity.Reward;

import java.util.List;

@Mapper
public interface RewardMapper {

    @Insert("INSERT INTO reward (carbon_point, reward, created_at, user_id) VALUES (#{carbonPoint}, #{reward}, #{createdAt}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "rewardId")
    void create(Reward reward);

    @Select("SELECT * FROM reward WHERE user_id = #{userId} AND reward_id < #{cursorId} ORDER BY reward_id DESC LIMIT 20")
    List<Reward> getList(@Param("userId") Long userId, @Param("cursorId") Long cursorId);

    @Select("SELECT SUM(reward) FROM reward WHERE user_id = #{userId}")
    Long getTotalCashback(@Param("userId") Long userId);

    Long getTotalCarbon(@Param("userId") Long userId);

    Long getTotalStar(@Param("userId") Long userId);

    String getCategory(@Param("userId") Long userId);
}
