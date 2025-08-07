package org.refit.spring.hospital.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.hospital.dto.*;
import org.refit.spring.hospital.enums.HospitalFilter;
import org.refit.spring.hospital.enums.HospitalSort;
import org.refit.spring.hospital.enums.HospitalType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitWebConfig(classes = RootConfig.class)
@Log4j
class HospitalServiceTest {

    @Autowired
    private HospitalService service;

    private final Long userId = 5L;

    @DisplayName("의료 영수증 필터 목록 조회")
    @Test
    void getFilteredList() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            MedicalListRequestDto dto = new MedicalListRequestDto();
            dto.setFilter(HospitalFilter.ALL);
            dto.setStartDate(sdf.parse("2022-01-01"));
            dto.setEndDate(sdf.parse("2025-08-01"));
            dto.setType(HospitalType.ALL);
            dto.setSort(HospitalSort.LATEST);
            dto.setSize(10L);

            MedicalReceiptListCursorDto result = service.getFilteredList(userId, dto);
            assertNotNull(result);
            assertNotNull(result.getHospitalList());
            System.out.println("조회된 건수: " + result.getHospitalList().size());
        } catch (Exception e) {
            fail("조회 실패: " + e.getMessage());
        }
    }

    @DisplayName("의료 영수증 상세 조회")
    @Test
    void findHospitalExpenseDetail() {
        Long receiptId = 1L;

        try {
            MedicalReceiptDetailDto result = service.findHospitalExpenseDetail(userId, receiptId);
            assertNotNull(result);
            System.out.println("병원명: " + result.getHospitalName());
        } catch (NoSuchElementException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }
    }

    @DisplayName("PDF 파일명 저장")
    @Test
    void updateHospitalVoucher() {
        MedicalImageFileNameDownloadDto dto = new MedicalImageFileNameDownloadDto();
        dto.setReceiptId(1L);
        dto.setMedicalImageFileName("testfile.pdf");

        assertDoesNotThrow(() -> service.updateHospitalVoucher(userId, dto));
        System.out.println("PDF 파일명 저장 성공");
    }

    @DisplayName("PDF 파일명 조회")
    @Test
    void findHospitalVoucher() {
        Long receiptId = 1L;

        MedicalImageFileNameCheckDto result = service.findHospitalVoucher(userId, receiptId);

        assertNotNull(result);
        System.out.println("조회된 파일명: " + result.getMedicalImageFileName());
    }

    @DisplayName("최근 병원비 및 보험청구 가능 건수 조회")
    @Test
    void getHospitalRecentInfo() {
        MedicalReceiptRecentDto result = service.getHospitalRecentInfo(userId);

        assertNotNull(result);
        System.out.println("총 병원비: " + result.getRecentTotalPrice());
        System.out.println("보험청구 가능 건수: " + result.getInsuranceBillable());
    }

    @DisplayName("가입된 보험 목록 조회")
    @Test
    void findInsuranceSubscribeById() {
        List<InsuranceSubscribedCheckDto> result = service.findInsuranceSubscribeById(userId);

        if (result == null || result.isEmpty()) {
            System.out.println("가입된 보험 없음");
        } else {
            result.forEach(r -> System.out.println("보험명: " + r.getInsuranceName()));
            assertFalse(result.isEmpty());
        }
    }

    @DisplayName("보험 청구 방문 정보 조회")
    @Test
    void getHospitalVisitInfo() {
        Long receiptId = 2020L;

        MedicalCheckDto result = service.getHospitalVisitInfo(userId, receiptId);

        assertNotNull(result);
        System.out.println("방문일자: " + result.getCreatedAt());
    }

    @DisplayName("보험 청구 요청 테스트")
    @Test
    void insertInsuranceClaim() {
        InsuranceClaimDto dto = new InsuranceClaimDto();
        dto.setReceiptId(1L);
        dto.setInsuranceId(1L);
        dto.setVisitedReason("감기");

        try {
            service.insertInsuranceClaim(dto, userId);
            System.out.println("보험 청구 성공");
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생: " + e.getMessage());
        }
    }
}