package org.refit.spring.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.refit.spring.receiptProcess.dto.CheckCompanyResponseDto;
import org.refit.spring.receiptProcess.dto.ReceiptProcessCheckDto;
import org.refit.spring.receiptProcess.dto.ReceiptSelectDto;

import java.util.List;

@Mapper
public interface ReceiptProcessMapper {

    // 사업장 선택 조회
    @Select("SELECT c.company_name AS companyName, e.start_date AS startDate, e.end_date AS endDate " +
            "FROM employee e " +
            "JOIN company c ON e.company_id = c.company_id " +
            "WHERE e.user_id = #{userId}")
    List<ReceiptSelectDto> findCompanySelectionListByUserId(@Param("userId") Long userId);

    // 영수 처리 정보 조회
    @Select("SELECT c.company_name AS companyName, c.address AS address " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "WHERE r.receipt_id = #{receiptId}")
    ReceiptProcessCheckDto findCompanyInfoByReceiptId(@Param("receiptId") Long receiptId);

    // 사업자 정보 확인 요청
    @Insert("INSERT INTO company (company_id, ceo_Id, company_name, ceo_name, address, opened_date, created_at, updated_at, category_id) " +
            "VALUES (#{companyId}, #{ceoId}, #{companyName}, #{ceoName}, #{address}, #{openedDate}, NOW(), NOW(), 0)")
    void insertVerifiedCompany(CheckCompanyResponseDto dto);

    // 영수 처리 요청
    @Insert("INSERT INTO receipt_process (process_state, ceo_id, progress_type, progress_detail, voucher, receipt_id, created_at) " +
            "VALUES ('inProgress', #{ceoId}, #{progressType}, #{progressDetail}, #{voucher}, #{receiptId}, NOW())")
    void insertReceiptProcess(
            @Param("ceoId") Long ceoId,
            @Param("progressType") String progressType,
            @Param("progressDetail") String progressDetail,
            @Param("voucher") String voucher,
            @Param("receiptId") Long receiptId
    );
}
