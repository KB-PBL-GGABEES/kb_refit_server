package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.ceo.CeoReceiptQueryProvider;
import org.refit.spring.ceo.CorporateCardQueryProvider;
import org.refit.spring.ceo.dto.CorporateCardListDto;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.dto.ReceiptProcessApplicantDto;
import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.receipt.dto.ReceiptContentDetailDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;

import java.util.Date;
import java.util.List;

@Mapper
public interface CeoMapper {

    // 영수 처리 항목에 추가
    @Insert("INSERT INTO receipt_process (process_state, ceo_id, created_at, updated_at, receipt_id) VALUES ('none', #{ceoId}, now(), now(), #{receiptId})")
    void insertProcess(@Param("ceoId") Long ceoId, @Param("userId") Long userId, @Param("receiptId") Long receiptId);

    @Select("SELECT ceo_id FROM company WHERE company_id = #{companyId}")
    Long findCeoId(@Param("companyId") Long companyId);

    // 경비 처리가 필요한 내역 조회
    @Select("SELECT\n" +
            "            r.receipt_id,\n" +
            "            c.company_name,\n" +
            "            r.created_at,\n" +
            "            r.total_price,\n" +
            "            p.process_state\n" +
            "        FROM receipt r\n" +
            "            JOIN company c ON r.company_id = c.company_id\n" +
            "            JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "WHERE p.process_state = 'inProgress'\n" +
            "  AND EXISTS (\n" +
            "    SELECT 1 FROM employee e\n" +
            "    WHERE e.user_id = r.user_id\n" +
            "      AND e.company_id IN (\n" +
            "        SELECT company_id FROM company WHERE ceo_id = #{userId})\n)" +
            "        ORDER BY r.receipt_id")
    List<Ceo> getPendingReceipts(@Param("userId") Long userId);

    // 경비 처리가 필요한 내역 개수
    @Select("SELECT COUNT(*) " +
            "FROM receipt r " +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id " +
            "WHERE p.process_state = 'inProgress'\n" +
            "  AND EXISTS (\n" +
            "    SELECT 1 FROM employee e\n" +
            "    WHERE e.user_id = r.user_id\n" +
            "      AND e.company_id IN (\n" +
            "        SELECT company_id FROM company WHERE ceo_id = #{userId}))")
    int countPendingReceipts(@Param("userId") Long userId);

    // 이번 달 경비 처리 완료 내역 개수
    @Select("SELECT COUNT(*) " +
            "FROM receipt r " +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id " +
            "WHERE p.process_state IN ('accepted', 'rejected')\n" +
            "  AND MONTH(r.created_at) = MONTH(CURDATE())\n" +
            "  AND YEAR(r.created_at) = YEAR(CURDATE())\n" +
            "  AND EXISTS (\n" +
            "    SELECT 1 FROM employee e\n" +
            "    WHERE e.user_id = r.user_id\n" +
            "      AND e.company_id IN (\n" +
            "        SELECT company_id FROM company WHERE ceo_id = #{userId}))")
    int countCompletedReceiptsThisMonth(@Param("userId") Long userId);

    // 영수처리 신청자 상세 조회
    @Select("SELECT\n" +
            "    u.user_id,\n" +
            "    u.name,\n" +
            "    p.progress_type AS documentType,\n" +
            "    p.progress_detail AS documentDetail,\n" +
            "    p.voucher AS imageFileName\n" +
            "FROM receipt r\n" +
            "JOIN user u ON r.user_id = u.user_id\n" +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "WHERE r.receipt_id = #{receiptId}")
    ReceiptProcessApplicantDto getReceiptProcessDetail(@Param("receiptId") Long receiptId);

    // 영수증 상세 내역 조회
    @Select("SELECT\n" +
            "  r.user_id,\n" +
            "  r.receipt_id,\n" +
            "  r.company_id,\n" +
            "  c.company_name,\n" +
            "  c.ceo_name,\n" +
            "  c.address,\n" +
            "  r.total_price,\n" +
            "  r.supply_price,\n" +
            "  r.surtax,\n" +
            "  r.transaction_type,\n" +
            "  r.created_at,\n" +
            "  IFNULL(p.process_state, 'none') AS process_state,\n" +
            "  cr.card_number,\n" +
            "  cr.is_corporate,\n" +
            "  IFNULL(p.rejected_reason, '') AS rejected_reason\n" +
            "FROM receipt r\n" +
            "JOIN company c ON r.company_id = c.company_id\n" +
            "JOIN card cr ON r.card_id = cr.card_id\n" +
            "LEFT JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "WHERE r.receipt_id = #{receiptId}")
    ReceiptDetailDto getReceiptDetailByReceiptId(@Param("receiptId") Long receiptId);

    // 구매항목 상세 조회
    @Select("SELECT rc.merchandise_id, m.merchandise_name, m.merchandise_price, rc.amount " +
            "FROM receipt_content rc " +
            "JOIN merchandise m ON rc.merchandise_id = m.merchandise_id " +
            "WHERE rc.receipt_id = #{receiptId}")
    List<ReceiptContentDetailDto> getReceiptContents(@Param("receiptId") Long receiptId);

    // 경비 처리 완료 내역 조회
    @SelectProvider(type = CeoReceiptQueryProvider.class, method = "buildFilteredQuery")
    List<Ceo> getCompletedReceipts(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            @Param("period") Integer period,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("processState") ProcessState processState,
            @Param("sort") Sort sort);

    // 처리 완료된 항목 이메일 전송
    @Select("SELECT COUNT(*)\n" +
            "FROM receipt r\n" +
            "JOIN receipt_process p ON r.receipt_id = p.receipt_id\n" +
            "WHERE p.process_state IN ('accepted', 'rejected')\n" +
            "  AND EXISTS (\n" +
            "    SELECT 1 FROM employee e\n" +
            "    WHERE e.user_id = r.user_id\n" +
            "      AND e.company_id IN (\n" +
            "        SELECT company_id FROM company WHERE ceo_id = #{userId}))")
    int countCompletedReceipts(@Param("userId") Long userId
    );

    // 영수 처리 승인 및 반려
    @Select("SELECT receipt_id FROM receipt_process WHERE receipt_process_id = #{receiptProcessId}")
    Long getReceiptProcessId(@Param("receiptProcessId") Long receiptProcessId);


    @Update("UPDATE receipt_process\n" +
            "SET process_state = #{progressState},\n" +
            "rejected_reason = #{rejectedReason},\n" +
            "updated_at = NOW()\n" +
            "WHERE receipt_process_id = #{receiptProcessId}")
    void updateProcessState(@Param("receiptProcessId") Long receiptProcessId,
                            @Param("progressState") String progressState,
                            @Param("rejectedReason") String rejectedReason);

    // 이번 달 법카 사용 금액 조회
    @Select("SELECT SUM(r.total_price) AS totalPrice " +
            "FROM receipt r " +
            "JOIN card c ON r.card_id = c.card_id " +
            "JOIN employee e ON r.user_id = e.user_id " +
            "JOIN employee emp ON emp.company_id = e.company_id " +
            "WHERE c.is_corporate = TRUE " +
            "AND emp.user_id = #{ceoId} " +
            "AND MONTH(r.created_at) = MONTH(CURDATE()) " +
            "AND YEAR(r.created_at) = YEAR(CURDATE())")
    Long getCorporateCardCostThisMonth(@Param("ceoId") Long ceoId);

    // 지난달 법카 사용 금액 조회
    @Select("SELECT SUM(r.total_price) AS lastMonth " +
            "FROM receipt r " +
            "JOIN card c ON r.card_id = c.card_id " +
            "JOIN employee e ON r.user_id = e.user_id " +
            "JOIN employee emp ON emp.company_id = e.company_id " +
            "WHERE c.is_corporate = TRUE " +
            "AND emp.user_id = #{ceoId} " +
            "AND MONTH(r.created_at) = MONTH(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)) " +
            "AND YEAR(r.created_at) = YEAR(DATE_SUB(CURDATE(), INTERVAL 1 MONTH))")
    Long getCorporateCardCostLastMonth(@Param("ceoId") Long ceoId);

    // 법카 내역 조회
    @SelectProvider(type = CorporateCardQueryProvider.class, method = "buildFilteredQuery")
    List<CorporateCardListDto> getCorporateCardReceipts(
            @Param("userId") Long userId,
            @Param("cursorId") Long cursorId,
            @Param("period") Integer period,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("rejectState") RejectState rejectState,
            @Param("sort") Sort sort);
}
