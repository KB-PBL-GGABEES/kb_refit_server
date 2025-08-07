package org.refit.spring.receipt;

import org.refit.spring.receipt.enums.ReceiptFilter;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.receipt.enums.ReceiptType;

import java.util.Map;

public class ReceiptQueryProvider {
    public static String buildFilteredQuery(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder("SELECT r.*, rp.process_state, c.company_name FROM receipt r LEFT OUTER JOIN receipt_process rp ON rp.receipt_id = r.receipt_id LEFT OUTER JOIN company c ON r.company_id = c.company_id WHERE r.user_id = #{userId}");
        sql.append(" AND c.category_id != 1");
        ReceiptSort sort = (ReceiptSort) params.get("sort");
        if (sort == null) {
            sort = ReceiptSort.LATEST;
            params.put("sort", sort);
        }
        if (sort == ReceiptSort.LATEST) sql.append(" AND r.receipt_id < #{cursorId}");
        else sql.append(" AND r.receipt_id > #{cursorId}");
        Integer period = (Integer) params.get("period");
        if (period != null && period > 0) sql.append(" AND r.created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH)");
        else if (params.get("startDate") != null && params.get("endDate") != null) {
            sql.append(" AND r.created_at >= #{startDate} AND r.created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY)");
        }
        ReceiptType type = (ReceiptType) params.get("type");
        if (type != null && type != ReceiptType.ALL) {
            if (type == ReceiptType.APPROVED) sql.append(" AND r.total_price > 0 ");
            else if (type == ReceiptType.CANCELED) sql.append(" AND r.total_price < 0 ");
        }

        ReceiptFilter filter = (ReceiptFilter) params.get("filter");
        if (filter != null) {
            if (filter.isProcessed()) sql.append(" AND rp.process_state = 'accepted' ");
            else sql.append(" AND (rp.process_state IS NULL or rp.process_state != 'accepted') ");
        }


        if (sort == ReceiptSort.LATEST) sql.append(" ORDER BY r.created_at DESC");
        else sql.append(" ORDER BY r.created_at ASC");
        sql.append(" LIMIT #{size}");

        return sql.toString();
    }
}
