package org.refit.spring.mapper;

import org.apache.ibatis.annotations.*;
import org.refit.spring.receiptProcess.dto.*;

import java.time.LocalDate;
import java.util.Date;
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



    // 사업자 진위 확인
    @Select("SELECT company_id, company_name, ceo_name, opened_date FROM company WHERE company_id = #{companyId}")
    CheckCompanyResponseDto findCompanyInfoByCompanyId(@Param("companyId") Long companyId);

    @Insert("INSERT IGNORE INTO employee (user_id, company_id, start_date) VALUES (#{userId}, #{companyId}, #{startDate})")
        void insertEmployeeIfNotExists(@Param("userId") Long userId,
                                       @Param("companyId") Long companyId,
                                       @Param("startDate") Date startDate);


    // 영수 처리 요청
    @Insert("INSERT INTO receipt_process (process_state, ceo_id, progress_type, progress_detail, voucher, receipt_id, created_at, updated_at) " +
            "VALUES ('inProgress', #{ceoId}, #{progressType}, #{progressDetail}, #{voucher}, #{receiptId}, NOW(), NOW())")
    void insertReceiptProcess(
            @Param("ceoId") Long ceoId,
            @Param("progressType") String progressType,
            @Param("progressDetail") String progressDetail,
            @Param("voucher") String voucher,
            @Param("receiptId") Long receiptId
    );
    // UPDATE (등록 내용 전체 갱신용)
    @Update("UPDATE receipt_process " +
            "SET progress_type = #{progressType}, " +
            "progress_detail = #{progressDetail}, " +
            "voucher = #{voucher}, " +
            "updated_at = NOW() " +
            "WHERE receipt_id = #{receiptId}")
    void updateReceiptProcess(ReceiptProcessRequestDto dto);


    // 상태만 변경
    @Update("UPDATE receipt_process " +
            "SET process_state = #{processState}, updated_at = NOW() " +
            "WHERE receipt_id = #{receiptId}")
    void updateProcessState(@Param("receiptId") Long receiptId,
                            @Param("processState") String processState);

    // receiptId가 실제로 존재하는지 확인
    @Select("SELECT COUNT(*) > 0 FROM receipt_process WHERE receipt_id = #{receiptId}")
    boolean existsReceiptProcessByReceiptId(@Param("receiptId") Long receiptId);

    // userId와 receiptId로 ceoId 찾기
    @Select("SELECT c.ceo_id " +
            "FROM receipt r " +
            "JOIN company c ON r.company_id = c.company_id " +
            "WHERE r.receipt_id = #{receiptId} AND r.user_id = #{userId}")
    Long findCeoIdByUserIdAndReceiptId(@Param("userId") Long userId,
                                       @Param("receiptId") Long receiptId);

    // 관련 이미지 파일명 DB조회
    @Select("SELECT voucher FROM receipt_process WHERE receipt_id = #{receiptId}")
    String findVoucherFileNameByReceiptId(@Param("receiptId") Long receiptId);

    @Select("SELECT rejected_reason FROM receipt_process WHERE receipt_id = #{receiptId}")
    String findReason(@Param("receiptId") Long receiptId);

}