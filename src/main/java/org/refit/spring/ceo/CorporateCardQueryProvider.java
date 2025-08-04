package org.refit.spring.ceo;


import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;

import java.util.Map;

public class CorporateCardQueryProvider {
    public static String buildFilteredQuery(Map<String, Object> params) {

        // 법카 내역 조회
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" r.receipt_id, r.total_price, r.created_at, cp.company_name, rp.process_state, r.card_id, c.is_corporate AS corporate");
        sql.append(" FROM receipt r");
        sql.append(" JOIN card c ON r.card_id = c.card_id");
        sql.append(" JOIN employee e ON r.user_id = e.user_id");
        sql.append(" JOIN company cp ON r.company_id = cp.company_id");
        sql.append(" LEFT JOIN receipt_process rp ON r.receipt_id = rp.receipt_id");
        sql.append(" WHERE c.is_corporate = TRUE");
        sql.append(" AND e.company_id = (SELECT company_id FROM employee WHERE user_id = #{userId} LIMIT 1)");

        // 필터 (전체, 미처리, 반려)
        RejectState rejectState = (RejectState) params.get("rejectState");
        if(rejectState != null && rejectState != RejectState.Whole) {
            if (rejectState.UnRejected()) {
                sql.append(" AND rp.process_state IS NULL ");
            } else if (rejectState.Rejected()) {
                sql.append(" AND rp.process_state = 'rejected' ");
            }
        }

        // 기간 (1, 3, 6개월, 직접입력)
        Integer period = (Integer) params.get("period");
        if (period != null && period > 0) {
            sql.append(" AND r.created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH)");
        } else if (params.get("startDate") != null && params.get("endDate") != null) {
            sql.append(" AND r.created_at >= #{startDate} AND r.created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY)");
        }

        // 정렬 (최신순, 과거순)
        Sort sort = (Sort) params.get("sort");
        if(sort == null) {  // 기본 정렬
            sort = Sort.Newest;
            params.put("sort", sort);
        }

        if(sort == Sort.Newest) {
            sql.append(" AND r.receipt_id < #{cursorId} ORDER BY r.created_at DESC");
        } else if(sort == Sort.Oldest) {
            sql.append(" AND r.receipt_id > #{cursorId} ORDER BY r.created_at ASC");
        }

        sql.append(" LIMIT 20");

        return sql.toString();
    }
}
