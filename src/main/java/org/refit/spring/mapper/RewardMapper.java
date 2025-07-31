package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.RewardQueryProvider;
import org.refit.spring.reward.entity.Reward;
import org.refit.spring.reward.enums.RewardType;

import java.util.Date;
import java.util.List;

@Mapper
public interface RewardMapper {

    @Insert("INSERT INTO reward (carbon_point, reward, created_at, user_id) VALUES (#{carbonPoint}, 0, #{createdAt}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "rewardId")
    void createCarbon(Reward reward);

    @Insert("INSERT INTO reward (carbon_point, reward, created_at, user_id) VALUES (0, #{reward}, #{createdAt}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "rewardId")
    void createReward(Reward reward);

    @SelectProvider(type = RewardQueryProvider.class, method = "buildFilteredQuery")
    List<Reward> getList(@Param("userId") Long userId,
                         @Param("cursorId") Long cursorId,
                         @Param("period") Integer period,
                         @Param("startDate") Date startDate,
                         @Param("endDate") Date endDate,
                         @Param("type") RewardType type,
                         @Param("sort") ReceiptSort sort);

    @Select("SELECT * FROM reward WHERE user_id = #{userId} AND reward_id < #{cursorId} AND created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH) ORDER BY reward_id DESC LIMIT 20")
    List<Reward> getListForMonths(@Param("userId") Long userId, @Param("cursorId") Long cursorId, @Param("period") int period);

    @Select("SELECT * FROM reward WHERE user_id = #{userId} AND reward_id < #{cursorId} AND created_at >= #{startDate} AND created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY) ORDER BY reward_id DESC LIMIT 20")
    List<Reward> getListWithPeriod(@Param("userId") Long userId, @Param("cursorId") Long cursorId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Select("SELECT IFNULL(SUM(reward), 0) FROM reward WHERE user_id = #{userId}")
    Long getTotalCashback(@Param("userId") Long userId);

    @Select("SELECT IFNULL(SUM(carbon_point), 0) FROM reward WHERE user_id = #{userId}")
    Long getTotalCarbon(@Param("userId") Long userId);

    @Select("SELECT IFNULL(category_name, '') FROM receipt r " +
            "INNER JOIN company co ON r.company_id = co.company_id " +
            "INNER JOIN categories ca ON co.category_id = ca.category_id " +
            "WHERE r.user_id = #{userId} GROUP BY ca.category_id ORDER BY COUNT(*) DESC LIMIT 1")
    String getCategory(@Param("userId") Long userId);

    @Select("SELECT wallet_cost FROM wallet_brand WHERE wallet_id = #{walletId}")
    Long getCost(@Param("walletId") Long walletId);

    @Insert("INSERT INTO personal_wallet_brand (is_mounted, created_at, wallet_id, user_id) VALUES (0, now(), #{walletId}, #{userId})")
    void createPersonal(@Param("userId") Long userId, @Param("walletId") Long walletId);

    @Update("UPDATE user SET total_star_point = #{totalStarPoint} WHERE user_id = #{userId}")
    void updateStarPoint(@Param("userId") Long userId, @Param("totalStarPoint") Long totalStarPoint);

    @Select("SELECT EXISTS(SELECT 1 FROM personal_wallet_brand WHERE user_id = #{userId} AND wallet_id = #{walletId})")
    boolean checkPossess(@Param("userId") Long userId, @Param("walletId") Long walletId);
}
