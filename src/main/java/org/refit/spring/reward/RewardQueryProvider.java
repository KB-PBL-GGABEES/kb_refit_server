package org.refit.spring.reward;

import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.enums.RewardType;

import java.util.Map;

public class RewardQueryProvider {
    public static String buildFilteredQuery(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("SELECT * FROM reward WHERE user_id = #{userId}");
        sql.append(" AND reward_id < #{cursorId}");
        Integer period = (Integer) params.get("period");
        if (period != null && period > 0) sql.append(" AND created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH)");
        else if (params.get("startDate") != null && params.get("endDate") != null) {
            sql.append(" AND created_at >= #{startDate} AND created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY)");
        }
        RewardType type = (RewardType) params.get("type");
        if (type != null && type != RewardType.전체) {
            if (type == RewardType.적립포인트) sql.append(" AND carbon_point > 0");
            else if (type == RewardType.할인금액) sql.append(" AND reward > 0");
        }
        ReceiptSort sort = (ReceiptSort) params.get("sort");
        if (sort == null) {
            sort = ReceiptSort.최신순;
            params.put("sort", sort);
        }
        if (sort == ReceiptSort.최신순) sql.append(" ORDER BY reward_id DESC");
        else sql.append(" ORDER BY reward_id ASC");
        sql.append(" LIMIT 20");

        return sql.toString();
    }
}
