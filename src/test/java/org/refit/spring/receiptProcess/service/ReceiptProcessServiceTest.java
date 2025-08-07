package org.refit.spring.receiptProcess.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.receiptProcess.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringJUnitWebConfig(classes = RootConfig.class)
class ReceiptProcessServiceTest {

    @Autowired
    ReceiptProcessService service;

    private final Long userId = 5L;

    @DisplayName("회사 선택 리스트 조회")
    @Test
    void getCompanySelectionListByUserId() {
        List<ReceiptSelectDto> list = service.getCompanySelectionListByUserId(userId);
        assertNotNull(list);
        assertFalse(list.isEmpty());
        list.forEach(company ->
                System.out.println("회사명: " + company.getCompanyName()
                        + ", 시작일: " + company.getStartDate()
                        + ", 종료일: " + company.getEndDate())
        );
    }

    @DisplayName("회사 정보 조회 - companyId 기준")
    @Test
    void getCompanyInfoByCompanyId() {
        // given
        Long companyId = 1L;

        // when
        ReceiptProcessCheckDto companyInfo = service.getCompanyInfoByCompanyId(companyId);

        // then
        assertNotNull(companyInfo);
        assertEquals(companyId, companyInfo.getCompanyId());
        System.out.println("회사명: " + companyInfo.getCompanyName());
        System.out.println("주소: " + companyInfo.getAddress());
    }

    @DisplayName("사업자 진위확인 요청")
    @Test
    void verifyAndRegisterEmployee() throws JsonProcessingException {
        CheckCompanyRequestDto dto = new CheckCompanyRequestDto();
        dto.setCompanyId(1018123456L); // 예시 사업자번호 (실제 존재하는 번호 필요)
        dto.setCeoName("홍길동");
        dto.setOpenedDate(parseDate("2020-01-01"));

        try {
            CheckCompanyResponseDto result = service.verifyAndRegisterEmployee(dto, userId);
            assertTrue(result.isValid());
            System.out.println("회사명: " + result.getCompanyName());
        } catch (NoSuchElementException e) {
            System.out.println("DB에 해당 companyId가 없음: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("유효하지 않은 요청: " + e.getMessage());
        }
    }

    @DisplayName("영수 처리 등록 또는 수정")
    @Test
    void upsertReceiptProcess() {
        ReceiptProcessRequestDto dto = new ReceiptProcessRequestDto();
        dto.setCompanyId(1L); // 실제 ceoId가 존재하는 companyId
        dto.setReceiptId(1L); // 실제 receipt_id
        dto.setProgressType("식대");
        dto.setProgressDetail("점심 식사");
        dto.setFileName("voucher_test_image.png");

        try {
            service.upsertReceiptProcess(dto, userId);
            System.out.println("영수 처리 등록 또는 수정 성공");
        } catch (Exception e) {
            fail("업데이트 실패: " + e.getMessage());
        }
    }

    private Date parseDate(String s) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}