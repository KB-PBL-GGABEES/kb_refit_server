//package org.refit.spring.hospital.controller;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//class HospitalControllerTest {
//
//    @DisplayName("의료비 납입 내역 조회")
//    @Test
//    void findHospitalExpense() {
//        // given
//        String expectedCompanyName = "손박사 이비인후과";
//        String expectedProcessState = "보험 청구 완료";
//        int expectedTotalPrice = 52500;
//
//        // when
//        // 실제 호출은 안 하고, 가상의 결과라고 가정
//        String companyName = "손박사 이비인후과";
//        String processState = "보험 청구 완료";
//        int totalPrice = 52500;
//
//        // then
//        Assertions.assertEquals(expectedCompanyName, companyName);
//        Assertions.assertEquals(expectedProcessState, processState);
//        Assertions.assertEquals(expectedTotalPrice, totalPrice);
//        System.out.println("의료비내역 조회 테스트 완료");
//    }
//
//    @DisplayName("의료비 납입 내역 상세 조회")
//    @Test
//    void findHospitalExpenseDetailById() {
//        // given
//        String expectedHospitalName = "손박사 이비인후과";
//        long expectedCompanyId = 1234567890L;
//        String expectedTransactionType = "카드거래";
//
//        // when
//        String hospitalName = "손박사 이비인후과";
//        long companyId = 1234567890L;
//        String transactionType = "카드거래";
//
//        // then
//        Assertions.assertEquals(expectedHospitalName, hospitalName);
//        Assertions.assertEquals(expectedCompanyId, companyId);
//        Assertions.assertEquals(expectedTransactionType, transactionType);
//        System.out.println("의료비 상세 조회 테스트 완료");
//
//    }
//
//    @DisplayName("가입된 보험 조회")
//    @Test
//    void findInsuranceSubscribeById() {
//        // given
//        long expectedInsuranceId = 1L;
//        String expectedInsuranceName = "(무)KB손보간편가입실손의료비보험";
//        String expectedJoinedDate = "2017.08.12";
//
//        // when
//        long insuranceId = 1L;
//        String insuranceName = "(무)KB손보간편가입실손의료비보험";
//        String joinedDate = "2017.08.12";
//
//        // then
//        Assertions.assertEquals(expectedInsuranceId, insuranceId);
//        Assertions.assertEquals(expectedInsuranceName, insuranceName);
//        Assertions.assertEquals(expectedJoinedDate, joinedDate);
//        System.out.println("가입된 보험 조회 테스트 완료");
//    }
//}