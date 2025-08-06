package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.hospital.dto.*;
import org.refit.spring.hospital.provider.HospitalQueryProvider;

import java.util.List;
import java.util.Date;
import java.util.Map;

@Mapper
public interface HospitalMapper {

    // 의료비 납입내역 조회
    @SelectProvider(type = HospitalQueryProvider.class, method = "buildFilteredQuery")
    List<MedicalReceiptDto> getFilteredList(Map<String, Object> params);


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
    List<MedicalReceiptDetailDto> findHospitalExpenseDetailByUserIdAndReceiptId(
            @Param("userId") Long userId,
            @Param("receiptId") Long receiptId
    );

    // 진료비 세부산정내역 PDF 파일명 DB저장
    @Update("UPDATE hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "SET hp.hospital_voucher = #{medicalImageFileName} " +
            "WHERE hp.receipt_id = #{receiptId} AND r.user_id = #{userId}")
    void updateHospitalVoucher(@Param("receiptId") Long receiptId,
                               @Param("medicalImageFileName") String medicalImageFileName,
                               @Param("userId") Long userId);

    @Insert("INSERT INTO hospital_process (receipt_id, process_state, hospital_voucher) VALUES (#{receiptId}, 'none', #{hospitalVoucher})")
    void insertEmptyHospitalProcess(@Param("receiptId") Long receiptId, @Param("hospitalVoucher") String hospitalVoucher);

    // 진료비 세부산정내역 PDF 파일명 조회
    @Select("SELECT hp.hospital_voucher " +
            "FROM hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "WHERE hp.receipt_id = #{receiptId} AND r.user_id = #{userId}")
    String findHospitalVoucherByReceiptId(@Param("receiptId") Long receiptId,
                                          @Param("userId") Long userId);

    // 최근 병원비 조회
    @Select("SELECT EXISTS(SELECT 1 FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "WHERE user_id = #{userId} " +
            "AND cat.category_id = 1) ")
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
    MedicalReceiptRecentDto findByHospitalRecentId(@Param("userId") Long userId);

    // 가입된 보험 조회
    @Select("SELECT insurance_id AS insuranceId, " +
            "insurance_name AS insuranceName, " +
            "joined_date AS joinedDate " +
            "FROM insurance " +
            "WHERE user_id = #{userId}")
    List<InsuranceSubscribedCheckDto> findByInsuranceSubscribeId(@Param("userId") Long userId);

   // 보험 청구_방문 정보
   @Select("SELECT c.company_name AS hospitalName, r.created_at AS createdAt " +
           "FROM receipt r " +
           "JOIN company c ON r.company_id = c.company_id " +
           "JOIN categories cat ON c.category_id = cat.category_id " +
           "WHERE r.receipt_id = #{receiptId} AND r.user_id = #{userId} AND cat.category_id = 1")
   MedicalCheckDto findHospitalVisitInfo(@Param("userId") Long userId, @Param("receiptId") Long receiptId);


    // 보험이 none인 경우에만 보험 청구 가능
    @Select("SELECT process_state FROM hospital_process WHERE receipt_id = #{receiptId}")
    String findProcessStateByReceiptId(@Param("receiptId") Long receiptId);

   // 보험 청구 내용 수정 (state=none일 때만)
    @Update("UPDATE hospital_process " +
            "SET process_state = #{processState}, sicked_date = #{sickedDate}, visited_reason = #{visitedReason}, insurance_id = #{insuranceId} " +
            "WHERE receipt_id = #{receiptId}")
    void updateInsuranceClaim(InsuranceClaimDto dto);

   // 보험 가입 여부 확인용 메서드
    @Select("SELECT COUNT(*) > 0 " +
            "FROM insurance " +
            "WHERE insurance_id = #{insuranceId} AND user_id = #{userId}")
    boolean existsUserInsurance(@Param("userId") Long userId,
                                @Param("insuranceId") Long insuranceId);


    // 보험 청구_Update
    @Update("UPDATE hospital_process " +
            "SET process_state = 'inProgress', " +
            "sicked_date = #{sickedDate}, " +
            "visited_reason = #{visitedReason}, " +
            "insurance_id = #{insuranceId} " +
            "WHERE receipt_id = #{receiptId}")
    void insertInsuranceClaim(InsuranceClaimDto dto);
}