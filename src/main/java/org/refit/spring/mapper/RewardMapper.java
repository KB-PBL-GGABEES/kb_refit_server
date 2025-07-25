package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.reward.entity.Reward;

import java.util.List;

@Mapper
public interface RewardMapper {
    @Select("SELECT * FROM reward WHERE reward_id < #{cursorId} ORDER BY reward_id DESC LIMIT 20")
    List<Reward> getList(Long cursorId);
}
