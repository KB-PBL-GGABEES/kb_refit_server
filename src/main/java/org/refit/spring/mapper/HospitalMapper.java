package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.HospitalRecentResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;

import java.util.List;
import java.util.Date;

@Mapper
public interface HospitalMapper {

//    // 의료비 납입내역 조회
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
//            "ORDER BY r.created_at DESC")
//            List<HospitalExpenseResponseDto> findByAllHospitalExpenseByUserId(@Param("userId") Long userId);

    // 첫 페이지: cursorDate 없이 최신순 20개
    @Select("SELECT " +
            "r.created_at AS createdAt, " +
            "c.company_name AS storeName, " +
            "hp.process_state AS processState, " +
            "r.total_price AS totalPrice " +
            "FROM hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "WHERE r.user_id = #{userId} " +
            "AND cat.category_id = 1 " +
            "ORDER BY r.created_at DESC " +
            "LIMIT 20")
    List<HospitalExpenseResponseDto> findFirstPage(@Param("userId") Long userId);

    // 커서 이후 페이지: created_at < cursorDate
    @Select("SELECT " +
            "r.created_at AS createdAt, " +
            "c.company_name AS storeName, " +
            "hp.process_state AS processState, " +
            "r.total_price AS totalPrice " +
            "FROM hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "WHERE r.user_id = #{userId} " +
            "AND cat.category_id = 1 " +
            "AND r.created_at < #{cursorDate} " +
            "ORDER BY r.created_at DESC " +
            "LIMIT 20")
    List<HospitalExpenseResponseDto> findByCursorDate(@Param("userId") Long userId, @Param("cursorDate") Date cursorDate);

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
            "WHERE r.receipt_id = #{receiptId} " +
            "AND cat.category_id = 1")
             HospitalExpenseDetailResponseDto findByHospitalExpenseDetailId(@Param("receiptId") Long receiptId);
    
    // 최근 병원비 조회
    @Select("SELECT COALESCE(SUM(r.total_price), 0) AS recentTotalPrice, " +
            "COUNT(CASE WHEN r.transaction_type = '보험청구' THEN 1 END) AS insuranceBillable " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "JOIN categories cat ON c.category_id = cat.category_id " +
            "WHERE cat.category_name = '병원' " +
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

    // 보험 청구 요청

}