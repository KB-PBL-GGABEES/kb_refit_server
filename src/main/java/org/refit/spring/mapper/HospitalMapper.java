package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.hospital.dto.*;

import java.util.List;
import java.util.Date;

@Mapper
public interface HospitalMapper {

    // 의료비 납입내역 조회
    // 기본 커서 기반 병원 영수증 목록 조회 (최근 순, LIMIT 10)
    @Select("SELECT " +
            "r.created_at AS createdAt, " +
            "c.company_name AS storeName, " +
            "hp.process_state AS processState, " +
            "r.total_price AS totalPrice, " +
            "r.receipt_id AS receiptId " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "LEFT JOIN hospital_process hp ON r.receipt_id = hp.receipt_id " +
            "WHERE r.user_id = #{userId} " +
            "AND cat.category_id = 1 " +
            "AND r.receipt_id < #{cursorId} " +
            "ORDER BY r.receipt_id DESC " +
            "LIMIT 10")
    List<HospitalExpenseResponseDto> findByCursorId(@Param("userId") Long userId,
                                                    @Param("cursorId") Long cursorId);
    // 최근 N개월 이내의 병원 영수증 목록 조회 (커서 기반)
    @Select("SELECT " +
            "r.created_at AS createdAt, " +
            "c.company_name AS storeName, " +
            "hp.process_state AS processState, " +
            "r.total_price AS totalPrice, " +
            "r.receipt_id AS receiptId " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "LEFT JOIN hospital_process hp ON r.receipt_id = hp.receipt_id " +
            "WHERE r.user_id = #{userId} " +
            "AND cat.category_id = 1 " +
            "AND r.receipt_id < #{cursorId} " +
            "AND r.created_at >= DATE_SUB(NOW(), INTERVAL #{period} MONTH) " +  // 최근 N개월 조건
            "ORDER BY r.receipt_id DESC " +
            "LIMIT 10")
    List<HospitalExpenseResponseDto> findByCursorIdWithinMonths(@Param("userId") Long userId,
                                                                @Param("cursorId") Long cursorId,
                                                                @Param("period") Integer period);
    // 시작일 ~ 종료일 사이 병원 영수증 목록 조회 (커서 기반)
    @Select("SELECT " +
            "r.created_at AS createdAt, " +
            "c.company_name AS storeName, " +
            "hp.process_state AS processState, " +
            "r.total_price AS totalPrice, " +
            "r.receipt_id AS receiptId " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "LEFT JOIN hospital_process hp ON r.receipt_id = hp.receipt_id " +
            "WHERE r.user_id = #{userId} " +
            "AND cat.category_id = 1 " +
            "AND r.receipt_id < #{cursorId} " +
            "AND r.created_at BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY r.receipt_id DESC " +
            "LIMIT 10")
    List<HospitalExpenseResponseDto> findByCursorIdWithPeriod(@Param("userId") Long userId,
                                                              @Param("cursorId") Long cursorId,
                                                              @Param("startDate") Date startDate,
                                                              @Param("endDate") Date endDate);


//    // 첫 페이지: cursorDate 없이 최신순 20개
//    @Select("SELECT " +
//            "r.created_at AS createdAt, " +
//            "c.company_name AS storeName, " +
//            "hp.process_state AS processState, " +
//            "r.total_price AS totalPrice " +
//            "FROM hospital_process hp " +
//            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
//            "JOIN company c ON r.company_id = c.company_id " +
//            "JOIN categories cat ON c.category_id = cat.category_id " +
//            "WHERE r.user_id = #{userId} " +
//            "AND cat.category_id = 1 " +
//            "ORDER BY r.created_at DESC " +
//            "LIMIT 2")
//    List<HospitalExpenseResponseDto> findFirstPage(@Param("userId") Long userId);
//
//    // 커서 이후 페이지: created_at < cursorDate
//    @Select("SELECT " +
//            "r.created_at AS createdAt, " +
//            "c.company_name AS storeName, " +
//            "hp.process_state AS processState, " +
//            "r.total_price AS totalPrice " +
//            "FROM hospital_process hp " +
//            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
//            "JOIN company c ON r.company_id = c.company_id " +
//            "JOIN categories cat ON c.category_id = cat.category_id " +
//            "WHERE r.user_id = #{userId} " +
//            "AND cat.category_id = 1 " +
//            "AND r.created_at < #{cursorDate} " +
//            "ORDER BY r.created_at DESC " +
//            "LIMIT 2")
//    List<HospitalExpenseResponseDto> findByCursorDate(@Param("userId") Long userId, @Param("cursorDate") Date cursorDate);

    // 의료비 납입 내역 상세 조회
    @Select("SELECT " +
            "c.company_name AS hospitalName, " +
            "c.company_id AS companyId, " +
            "c.ceo_name AS ceoName, " +
            "c.address AS address, " +
            "r.supply_price AS supplyPrice, " +
            "r.surtax AS surtax, " +
            "r.transaction_type AS transactionType, " +
            "r.created_at AS createdAt, " +
            "hp.process_state AS processState, " +
            "hp.sicked_date AS sickedDate, " +
            "hp.visited_reason AS visitedReason, " +
            "i.insurance_name AS insuranceName " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "LEFT JOIN hospital_process hp ON r.receipt_id = hp.receipt_id " +
            "LEFT JOIN insurance i ON hp.insurance_id = i.insurance_id " +
            "WHERE r.user_id = #{userId} " +
            "AND r.receipt_id = #{receiptId} " +
            "AND cat.category_id = 1")
    List<HospitalExpenseDetailResponseDto> findHospitalExpenseDetailByUserIdAndReceiptId(
            @Param("userId") Long userId,
            @Param("receiptId") Long receiptId
    );

    // 진료비 세부산정내역 PDF 파일명 DB저장
    @Update("UPDATE hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "SET hp.hospital_voucher = #{hospitalVoucher} " +
            "WHERE hp.receipt_id = #{receiptId} AND r.user_id = #{userId}")
    void updateHospitalVoucher(@Param("receiptId") Long receiptId,
                               @Param("hospitalVoucher") String hospitalVoucher,
                               @Param("userId") Long userId);
    // 진료비 세부산정내역 PDF 파일명 조회
    @Select("SELECT hp.hospital_voucher " +
            "FROM hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "WHERE hp.receipt_id = #{receiptId} AND r.user_id = #{userId}")
    String findHospitalVoucherByReceiptId(@Param("receiptId") Long receiptId,
                                          @Param("userId") Long userId);

    // 최근 병원비 조회
    @Select("SELECT EXISTS(SELECT 1 FROM receipt WHERE user_id = #{userId})")
    boolean existsUserReceipt(@Param("userId") Long userId);

    @Select("SELECT " +
            "COALESCE(SUM(r.total_price), 0) AS recentTotalPrice, " +
            "COUNT(CASE " +
            "  WHEN (hp.insurance_id IS NULL OR hp.process_state IS NULL OR hp.process_state = 'none') THEN 1 " +
            "  ELSE NULL " +
            "END) AS insuranceBillable " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "LEFT JOIN hospital_process hp ON r.receipt_id = hp.receipt_id " +
            "WHERE cat.category_id = 1 " +
            "AND r.created_at >= DATE_SUB(NOW(), INTERVAL 3 YEAR) " +
            "AND r.user_id = #{userId}")
    HospitalRecentResponseDto findByHospitalRecentId(@Param("userId") Long userId);

    // 가입된 보험 조회
    @Select("SELECT insurance_id AS insuranceId, " +
            "insurance_name AS insuranceName, " +
            "joined_date AS joinedDate " +
            "FROM insurance " +
            "WHERE user_id = #{userId}")
    List<InsuranceSubscribedResponseDto> findByInsuranceSubscribeId(@Param("userId") Long userId);

   // 보험 청구_방문 정보
   @Select("SELECT c.company_name AS hospitalName, r.created_at AS createdAt " +
           "FROM receipt r " +
           "JOIN company c ON r.company_id = c.company_id " +
           "JOIN categories cat ON c.category_id = cat.category_id " +
           "WHERE r.receipt_id = #{receiptId} AND r.user_id = #{userId} AND cat.category_id = 1")
   InsuranceVisit findHospitalVisitInfo(@Param("userId") Long userId, @Param("receiptId") Long receiptId);


    // 보험이 none인 경우에만 보험 청구 가능
    @Select("SELECT process_state FROM hospital_process WHERE receipt_id = #{receiptId}")
    String findProcessStateByReceiptId(@Param("receiptId") Long receiptId);

   // 보험 청구 내용 수정 (state=none일 때만)
    @Update("UPDATE hospital_process " +
            "SET process_state = #{processState}, sicked_date = #{sickedDate}, visited_reason = #{visitedReason}, insurance_id = #{insuranceId} " +
            "WHERE receipt_id = #{receiptId}")
    void updateInsuranceClaim(InsuranceClaimRequestDto dto);

   // 보험 가입 여부 확인용 메서드
    @Select("SELECT COUNT(*) > 0 " +
            "FROM insurance " +
            "WHERE insurance_id = #{insuranceId} AND user_id = #{userId}")
    boolean existsUserInsurance(@Param("userId") Long userId,
                                @Param("insuranceId") Long insuranceId);


    // 보험 청구_POST
    @Update("UPDATE hospital_process " +
            "SET process_state = 'inProgress', " +
            "sicked_date = #{sickedDate}, " +
            "visited_reason = #{visitedReason}, " +
            "insurance_id = #{insuranceId} " +
            "WHERE receipt_id = #{receiptId}")
    void insertInsuranceClaim(InsuranceClaimRequestDto dto);

}