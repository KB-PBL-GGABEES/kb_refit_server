package org.refit.spring.receipt;

import org.refit.spring.receipt.enums.ReceiptFilter;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.receipt.enums.ReceiptType;

import java.util.Map;

public class ReceiptQueryProvider {
    public static String buildFilteredQuery(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("SELECT * FROM receipt WHERE user_id = #{userId}");

        sql.append(" AND receipt_id < #{cursorId}");

        Integer period = (Integer) params.get("period");
        if (period != null && period > 0) sql.append(" AND created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH)");
        else if (params.get("startDate") != null && params.get("endDate") != null) {
            sql.append(" AND created_at >= #{startDate} AND created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY)");
        }
        ReceiptType type = (ReceiptType) params.get("type");
        if (type != null && type != ReceiptType.전체) {
            if (type == ReceiptType.승인) sql.append(" AND transaction_type = '카드 결제' ");
            else if (type == ReceiptType.취소) sql.append(" AND transaction_type = '환불' ");
        }

        ReceiptFilter filter = (ReceiptFilter) params.get("filter");
        if (filter != null) {
            if (filter.isProcessed()) sql.append(" AND EXISTS (SELECT 1 FROM receipt_process rp WHERE rp.receipt_id = receipt.receipt_id AND rp.process_state = 'accepted') ");
            else sql.append(" AND NOT EXISTS (SELECT 1 FROM receipt_process rp WHERE rp.receipt_id = receipt.receipt_id AND rp.process_state != 'accepted') ");
        }

        ReceiptSort sort = (ReceiptSort) params.get("sort");
        if (sort == null) sort = ReceiptSort.최신순;
        if (sort == ReceiptSort.최신순) sql.append(" ORDER BY receipt_id DESC");
        else sql.append(" ORDER BY receipt_id ASC");
        sql.append(" LIMIT 20");

        return sql.toString();
    }
}
