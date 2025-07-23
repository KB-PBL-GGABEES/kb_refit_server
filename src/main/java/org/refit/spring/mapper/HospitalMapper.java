package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.hospital.dto.HospitalExpenseDetailResponseDto;
import org.refit.spring.hospital.dto.HospitalExpenseResponseDto;
import org.refit.spring.hospital.dto.InsuranceSubscribedResponseDto;

import java.util.List;

@Mapper
public interface HospitalMapper {

    // 의료비 납입내역 조회
    @Select("SELECT r.created_at AS createdAt, " +
            "c.company_name AS storeName, " +
            "hp.process_state AS processState, " +
            "r.total_price AS totalPrice " +
            "FROM hospital_process hp " +
            "JOIN receipt r ON hp.receipt_id = r.receipt_id " +
            "JOIN company c ON r.company_id = c.company_id " +
            "WHERE hp.hospital_process_id = #{hospitalProcessId}")
             HospitalExpenseResponseDto findByHospitalProcessId(@Param("hospitalProcessId") Long hospitalProcessId);

    // 의료비 납입 내역 상세 조회
     @Select("SELECT c.company_name AS hospitalName, " +
        "c.company_id AS companyId, " +
        "c.boss_name AS bossName, " +
        "c.address AS address, " +
        "r.supply_price AS supplyPrice, " +
        "r.surtax AS surtax, " +
        "r.transaction_type AS transactionType, " +
        "r.created_at AS createdAt " +
        "FROM receipt r " +
        "JOIN company c ON r.company_id = c.company_id " +
        "WHERE r.receipt_id = #{receiptId}")
         HospitalExpenseDetailResponseDto findByHospitalExpenseDetailId(@Param("receiptId") Long receiptId);

    // 최근 병원비 조회

    // 가입된 보험 조회
    @Select("SELECT insurance_id AS insuranceId, " +
            "insurance_name AS insuranceName, " +
            "DATE_FORMAT(joined_date, '%Y.%m.%d') AS joinedDate " +
            "FROM insurance " +
            "WHERE user_id = #{userId}")
    List<InsuranceSubscribedResponseDto> findByInsuranceSubscribeId(@Param("userId") Long userId);

    // 보험 청구 요청

}