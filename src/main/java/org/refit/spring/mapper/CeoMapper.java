package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.receipt.entity.Receipt;

import java.time.LocalDateTime;
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
            "        ORDER BY r.receipt_id DESC")
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
            "        r.receipt_id AS receiptId\n" +
            "    FROM receipt r\n" +
            "    JOIN user u ON r.user_id = u.user_id\n" +
            "    JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "    WHERE r.receipt_id = #{receiptId}\n" +
            "    LIMIT 1")
    ReceiptDetailDto getReceiptDetail(
            @Param("userId") Long userId,
            Long receiptId);

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
            "  AND r.created_at >= #{fromDate}\n" +
            "  AND r.created_at  < #{cursor}\n" +
            "ORDER BY r.created_at DESC LIMIT 20")
    List<Ceo> getCompletedReceipts(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("cursor") LocalDateTime cursor,
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

    // 한달 법카 금액 조회

    // 법카 내역 조회
}
