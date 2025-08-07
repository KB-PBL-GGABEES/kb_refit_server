package org.refit.spring.ceo.provider;

import org.refit.spring.ceo.enums.State;
import org.refit.spring.ceo.enums.Sort;

import java.util.Map;

public class CeoReceiptQueryProvider {
    public static String buildFilteredQuery(Map<String, Object> params) {

        // 경비 처리 완료 내역 조회
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(" r.receipt_id, c.company_name, r.created_at, r.total_price,");
        sql.append(" CASE WHEN p.process_state = 'accepted' THEN 'accepted'");
        sql.append(" WHEN p.process_state = 'rejected' THEN 'rejected' ELSE p.process_state END AS process_state");
        sql.append(" FROM receipt r");
        sql.append(" JOIN company c ON r.company_id = c.company_id");
        sql.append(" JOIN receipt_process p ON r.receipt_id = p.receipt_id");
        sql.append(" JOIN card cd ON r.card_id = cd.card_id");
        sql.append(" WHERE p.process_state IN ('accepted', 'rejected')");
        sql.append(" AND cd.is_corporate = FALSE");

        sql.append(" AND EXISTS (");
        sql.append(" SELECT 1 FROM employee e");
        sql.append(" WHERE e.user_id = r.user_id");
        sql.append(" AND e.company_id IN (SELECT company_id FROM company WHERE ceo_id = #{userId}))");

        // 필터 (전체, 경비 승인, 경비 기각)
        State state = (State) params.get("state");
        if (state != null && state != State.Whole) {
            if(state.Process()) {
                sql.append(" AND p.process_state = 'accepted'");
            } else if(state.UnProcess()) {
                sql.append(" AND p.process_state = 'rejected'");
            }
        }

        // 기간 (1, 3, 6개월, 직접입력)
        if (params.get("period") != null) {
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

        sql.append(" LIMIT #{size} ");

        return sql.toString();
    }
}
