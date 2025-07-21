package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.ceo.domain.CeoVO;

import java.util.List;

@Mapper
public interface CeoMapper {

    // 경비 처리가 필요한 내역 조회
    @Select("        SELECT\n" +
            "            r.receipt_id,\n" +
            "            c.company_name,\n" +
            "            DATE_FORMAT(r.created_at, '%Y.%m.%d') AS receipt_date,\n" +
            "            DATE_FORMAT(r.created_at, '%H:%i') AS receipt_time,\n" +
            "            r.total_price\n" +
            "        FROM receipt r\n" +
            "                 JOIN company c ON r.company_id = c.company_id\n" +
            "        WHERE r.receipt_id NOT IN (\n" +
            "            SELECT receipt_id FROM receipt_process WHERE process_state IS NOT NULL\n" +
            "        )\n" +
            "        ORDER BY r.created_at DESC")
    List<CeoVO> getListUndone();

    // 경비 청구 항목 상세 조회

    // 경비 처리 완료 내역 조회

    // 처리 완료된 항목 이메일 전송

    // 영수 처리 승인

    // 영수 처리 반려

    // 한달 법카 금액 조회

    // 법카 내역 조회

    // 페이지네이션
}
