package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.CorporateCardListDto;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.dto.ReceiptListDto;
import org.refit.spring.ceo.entity.ReceiptProcess;
import org.refit.spring.receipt.entity.Receipt;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CeoMapper {

    // 영수 처리 항목에 추가
    @Insert("INSERT INTO receipt_process (process_state, ceo_id, created_at, receipt_id) VALUES ('none', #{ceoId}, now(), #{receiptId})")
    void insertProcess(@Param("ceoId") Long ceoId, @Param("userId") Long userId, @Param("receiptId") Long receiptId);

    @Select("SELECT ceo_id FROM company WHERE company_id = #{companyId}")
    Long findCeoId(@Param("companyId") Long companyId);

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
            "        ORDER BY r.receipt_id")
    List<Ceo> getPendingReceipts(@Param("userId") Long userId);

    // 경비 처리가 필요한 내역 개수
    @Select("SELECT COUNT(*) " +
            "FROM receipt r " +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id " +
            "WHERE p.process_state = 'none' AND r.user_id = #{userId}")
    int countPendingReceipts(@Param("userId") Long userId);

    // 이번 달 경비 처리 완료 내역 개수
    @Select("SELECT COUNT(*) " +
            "FROM receipt r " +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id " +
            "WHERE p.process_state IN ('accepted', 'rejected') " +
            "AND MONTH(r.created_at) = MONTH(CURDATE()) " +
            "AND YEAR(r.created_at) = YEAR(CURDATE()) " +
            "AND r.user_id = #{userId}")
    int countCompletedReceiptsThisMonth(@Param("userId") Long userId);

    // 경비 청구 항목 상세 조회
    @Select("SELECT \n" +
            "        u.user_id AS userId,\n" +
            "        u.name AS name,\n" +
            "        p.progress_type AS progressType,\n" +
            "        p.progress_detail AS progressDetail,\n" +
            "        p.voucher,\n" +
            "        r.receipt_id AS receiptId,\n" +
            "        p.process_state\n" +
            "    FROM receipt r\n" +
            "    JOIN user u ON r.user_id = u.user_id\n" +
            "    JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "    WHERE r.receipt_id = #{receiptId}\n" +
            "    LIMIT 1")
    ReceiptListDto getReceiptList(
            @Param("receiptId") Long receiptId,
            @Param("userId") Long userId);

    // 경비 처리 완료 내역 조회
    @Select("SELECT\n" +
            "    r.receipt_id,\n" +
            "    c.company_name,\n" +
            "    r.created_at AS receipt_date_time,\n" +
            "    r.total_price,\n" +
            "    CASE\n" +
            "        WHEN p.process_state = 'accepted' THEN '승인'\n" +
            "        WHEN p.process_state = 'rejected' THEN '거절'\n" +
            "        ELSE p.process_state\n" +
            "    END AS process_state\n" +
            "FROM receipt r\n" +
            "    JOIN company c ON r.company_id = c.company_id\n" +
            "    JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "WHERE p.process_state IN ('accepted', 'rejected')\n" +
            "  AND r.receipt_id < #{cursor}\n" +
            "ORDER BY r.receipt_id DESC LIMIT 20")
    List<Ceo> getCompletedReceipts(
            @Param("cursor") Long cursor,
            @Param("userId") Long userId);

    // 처리 완료된 항목 이메일 전송
    @Select("SELECT COUNT(*)\n" +
            "FROM receipt r\n" +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "WHERE p.process_state IN ('accepted', 'rejected')")
    int countCompletedReceipts(@Param("userId") Long userId
    );

    // 영수 처리 승인 및 반려
    @Update("UPDATE receipt_process\n" +
            "SET process_state = #{progressState},\n" +
            "rejected_reason = #{rejectedReason},\n" +
            "updated_at = NOW()\n" +
            "WHERE receipt_process_id = #{receiptProcessId}")
    void updateProcessState(@Param("receiptProcessId") Long receiptProcessId,
                            @Param("progressState") String progressState,
                            @Param("rejectedReason") String rejectedReason,
                            @Param("userId") Long userId);

    // 이번 달 법카 사용 금액 조회
    @Select("SELECT SUM(r.total_price) AS totalPrice\n" +
            "    FROM receipt r\n" +
            "    JOIN card c ON r.card_id = c.card_id\n" +
            "    JOIN employee e ON r.user_id = e.user_id\n" +
            "    WHERE c.is_corporate = TRUE\n" +
            "      AND e.company_id = (\n" +
            "          SELECT company_id FROM employee WHERE user_id = #{ceoId} LIMIT 1\n" +
            "      )\n" +
            "      AND MONTH(r.created_at) = MONTH(CURDATE())\n" +
            "      AND YEAR(r.created_at) = YEAR(CURDATE())")
    Long getCorporateCardCostThisMonth(@Param("ceoId") Long ceoId);

    // 지난달 법카 사용 금액 조회
    @Select("SELECT SUM(r.total_price) AS lastMonth\n" +
            "    FROM receipt r\n" +
            "    JOIN card c ON r.card_id = c.card_id\n" +
            "    JOIN employee e ON r.user_id = e.user_id\n" +
            "    WHERE c.is_corporate = TRUE\n" +
            "      AND e.company_id = (\n" +
            "          SELECT company_id FROM employee WHERE user_id = #{ceoId} LIMIT 1\n" +
            "      )\n" +
            "      AND MONTH(r.created_at) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))\n" +
            "      AND YEAR(r.created_at) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))")
    Long getCorporateCardCostLastMonth(@Param("ceoId") Long ceoId);

    // 법카 내역 조회
    @Select("SELECT \n" +
            "    r.receipt_id,\n" +
            "    r.total_price,\n" +
            "    r.created_at AS receipt_date_time,\n" +
            "    cp.company_name,\n" +
            "    rp.process_state,\n" +
            "    r.card_id, \n" +
            "    c.is_corporate AS corporate \n" +
            "FROM receipt r\n" +
            "JOIN card c ON r.card_id = c.card_id\n" +
            "JOIN employee e ON r.user_id = e.user_id\n" +
            "JOIN company cp ON r.company_id = cp.company_id\n" +
            "LEFT JOIN receipt_process rp ON r.receipt_id = rp.receipt_id\n" +
            "WHERE c.is_corporate = TRUE\n" +
            "  AND e.company_id = (\n" +
            "      SELECT company_id FROM employee WHERE user_id = #{userId} LIMIT 1)\n" +
            "  AND r.receipt_id < #{cursor}\n" +
            "ORDER BY r.receipt_id DESC LIMIT 20")
    List<CorporateCardListDto> getCorporateCardReceipts(
                    @Param("cursor") Long cursor,
                    @Param("userId") Long userId);
}
