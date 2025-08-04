package org.refit.spring.ceo.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class })
@Log4j
class CeoServiceImplTest {


//    @Test
//    public void getList() {
//        for(BoardDTO board : service.getList()) {
//            log.info(board);
//        }
//    }

    @Autowired
    private CeoService ceoService;

    // 경비 처리가 필요한 내역 조회
    @Test
    void getPendingReceipts() {
    }

    // 경비 처리가 필요한 내역 개수 세기
    @Test
    void countPendingReceipts() {
    }

    // 이번달 경비 처리가 완료된 내역 개수 세기
    @Test
    void countCompletedReceiptsThisMonth() {
    }

    // 경비 청구 항목 상세 조회
    @Test
    void getReceiptList() {
    }

    // 경비 청구 완료 내역 조회
    @Test
    void getCompletedReceipts() {
    }

    // 경비 청구 완료 항목 개수 세기
    @Test
    void countCompletedReceipts() {
    }

    // 경비 청구가 완료된 항목 이메일 보내기
    @Test
    void sendEmail() {
    }

    //
    @Test
    void receiptProcessing() {
    }

    @Test
    void getCorporateCardCost() {
    }

    @Test
    void getCorporateCardReceipts() {
    }
}