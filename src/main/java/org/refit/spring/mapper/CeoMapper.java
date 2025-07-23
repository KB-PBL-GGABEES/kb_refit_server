package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.dto.ReceiptDetailDto;

import java.util.List;

@Mapper
public interface CeoMapper {

    // 경비 처리가 필요한 내역 조회
    @Select("SELECT\n" +
            "            r.receipt_id,\n" +
            "            c.company_name,\n" +
            "            r.created_at AS receipt_date_time,\n" +
            "            r.total_price,\n" +
            "            p.process_state\n" +
            "        FROM receipt r\n" +
            "            JOIN company c ON r.company_id = c.company_id\n" +
            "            JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "        WHERE p.process_state = 'none'\n" +
            "        ORDER BY r.created_at DESC")
    List<Ceo> getListUndone();

    // 경비 청구 항목 상세 조회
    @Select("SELECT \n" +
            "        u.user_id AS userId,\n" +
            "        u.name AS name,\n" +
            "        p.progress_type AS progressType,\n" +
            "        p.progress_detail AS progressDetail,\n" +
            "        r.receipt_id AS receiptId\n" +
            "    FROM receipt r\n" +
            "    JOIN user u ON r.user_id = u.user_id\n" +
            "    JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "    WHERE r.receipt_id = #{receiptId}\n" +
            "    LIMIT 1")
    ReceiptDetailDto getReceiptDetail(Long receiptId);

    // 경비 처리 완료 내역 조회

    // 처리 완료된 항목 이메일 전송

    // 영수 처리 승인

    // 영수 처리 반려

    // 한달 법카 금액 조회

    // 법카 내역 조회

    // 페이지네이션
}
