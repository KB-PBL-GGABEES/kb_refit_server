package org.refit.spring.hospital.provider;

import org.refit.spring.hospital.enums.HospitalFilter;
import org.refit.spring.hospital.enums.HospitalSort;
import org.refit.spring.hospital.enums.HospitalType;

import java.util.Map;

public class HospitalQueryProvider {

    public static String buildFilteredQuery(Map<String, Object> params) {
        StringBuilder sql = new StringBuilder(
                "SELECT r.created_at AS createdAt, c.company_name AS storeName, " +
                        "hp.process_state AS processState, r.total_price AS totalPrice, r.receipt_id AS receiptId " +
                        "FROM receipt r " +
                        "JOIN company c ON r.company_id = c.company_id " +
                        "JOIN categories cat ON c.category_id = cat.category_id " +
                        "LEFT JOIN hospital_process hp ON r.receipt_id = hp.receipt_id " +
                        "WHERE r.user_id = #{userId} AND cat.category_id = 1"
        );

        HospitalSort sort = (HospitalSort) params.get("sort");
        Long cursorId = (Long) params.get("cursorId");

        if (cursorId != null) {
            if (sort == HospitalSort.LATEST) {
                sql.append(" AND r.receipt_id < #{cursorId}");
            } else {
                sql.append(" AND r.receipt_id > #{cursorId}");
            }
        }

        // 날짜 필터 (기간 우선)
        if (params.get("period") != null) {
            sql.append(" AND r.created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH)");
        } else if (params.get("startDate") != null && params.get("endDate") != null) {
            sql.append(" AND r.created_at >= #{startDate} AND r.created_at < DATE_ADD(#{endDate}, INTERVAL 1 DAY)");
        }

        // 거래 타입 필터
        HospitalType type = (HospitalType) params.get("type");
        if (type != null && type != HospitalType.ALL) {
            if (type == HospitalType.APPROVED)
                sql.append(" AND r.transaction_type = '카드 결제'");
            else if (type == HospitalType.CANCELED)
                sql.append(" AND r.transaction_type = '환불'");
        }

        // 처리 여부 필터
        HospitalFilter filter = (HospitalFilter) params.get("filter");

        if (filter != null && filter != HospitalFilter.ALL) {
            if (filter.isProcessed()) {
                // 처리된 상태: accepted, rejected
                sql.append(" AND hp.process_state IN ('accepted', 'rejected') ");
            } else {
                // 처리되지 않은 상태: NULL, none, inProgress
                sql.append(" AND (hp.process_state IS NULL OR hp.process_state IN ('none', 'inProgress')) ");
            }
        }

        // 정렬
        if (sort == HospitalSort.OLDEST) {
            sql.append(" ORDER BY r.created_at ASC");
        } else {
            sql.append(" ORDER BY r.created_at DESC");
        }

        sql.append(" LIMIT #{size} ");
        return sql.toString();
    }
}