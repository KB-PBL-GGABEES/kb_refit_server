package org.refit.spring.reward.provider;

import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.enums.RewardType;

import java.util.Map;

public class RewardQueryProvider {
    public static String buildFilteredQuery(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("SELECT * FROM reward WHERE user_id = #{userId}");
        ReceiptSort sort = (ReceiptSort) params.get("sort");
        if (sort == null) {
            sort = ReceiptSort.LATEST;
            params.put("sort", sort);
        }
        sql.append(" AND (reward != 0 OR carbon_point != 0)");
        if (sort == ReceiptSort.LATEST) sql.append(" AND reward_id < #{cursorId}");
        else sql.append(" AND reward_id > #{cursorId}");
        Integer period = (Integer) params.get("period");
        if (period != null && period > 0) sql.append(" AND created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH)");
        else if (params.get("startDate") != null && params.get("endDate") != null) {
            sql.append(" AND created_at >= #{startDate} AND created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY)");
        }
        RewardType type = (RewardType) params.get("type");
        if (type != null && type != RewardType.ALL) {
            if (type == RewardType.CARBONPOINT) sql.append(" AND carbon_point != 0");
            else if (type == RewardType.CASHBACK) sql.append(" AND reward != 0");
        }
        if (sort == ReceiptSort.LATEST) sql.append(" ORDER BY created_at DESC");
        else sql.append(" ORDER BY created_at ASC");
        sql.append(" LIMIT #{size}");

        return sql.toString();
    }
}
