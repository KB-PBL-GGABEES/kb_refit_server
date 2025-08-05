package org.refit.spring.ceo.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j
class CeoServiceImplTest {

    @Autowired
    private CeoService ceoService;

    @DisplayName("경비 처리가 필요한 내역 조회")
    @Test
    void getPendingDetail() {

    }

    @DisplayName("경비 청구 항목 상세 조회")
    @Test
    void getReceiptList() {
    }

    @DisplayName("경비 처리 완료 내역 조회")
    @Test
    void getCompletedReceipts() {
    }

    @DisplayName("처리 완료된 항목 이메일 전송")
    @Test
    void sendEmail() {
    }

    @DisplayName("영수 처리 승인 및 반려")
    @Test
    void receiptProcessing() {
    }

    @DisplayName("한달 법카 금액 조회")
    @Test
    void getCorporateCardTotalPrice() {
    }

    @DisplayName("법카 내역 조회")
    @Test
    void getCorporateCardReceipts() {
    }
}