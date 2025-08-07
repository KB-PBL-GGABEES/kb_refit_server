package org.refit.spring.ceo.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.ceo.dto.*;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.config.RootConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringJUnitWebConfig(classes = {RootConfig.class})
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j
class CeoServiceImplTest {

    @Autowired
    private CeoService ceoService;

    @DisplayName("경비 청수할 목록, 개수, 완료 개수 조회")
    @Test
    void getPendingDetail() {
        Long userId = 1L;
        PendingDetailDto result = ceoService.getPendingDetail(userId);

        log.info("경비 청구할 개수 : " + result.getCountPendingReceipts());
        log.info("이번 달 완료 개수 : " + result.getCountCompletedReceiptsThisMonth());
        log.info("경비 청구할 목록 : " + result.getPendingReceipts());

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(userId);
    }

    @DisplayName("영수증 상세, 신청자 조회")
    @Test
    void getReceiptList() {
        Long receiptId = 1L;
        ReceiptProcessDetailDto result = ceoService.getReceiptList(receiptId);

        log.info("상세정보 : " + result.getReceiptDetail());
        log.info("신청자정보 : " + result.getReceiptProcessApplicant());

        assertThat(result).isNotNull();
        assertThat(result.getReceiptDetail().getReceiptId()).isEqualTo(receiptId);
    }

    @DisplayName("커서 기반 경비 완료 내역 조회")
    @Test
    void getCompletedReceipts() {
        Long userId = 1L;
        ReceiptFilterDto filter = new ReceiptFilterDto();
        filter.setCursorId(Long.MAX_VALUE);
        filter.setSort(Sort.Newest);
        filter.setSize(10L);

        ReceiptListCursorDto result = ceoService.getCompletedReceipts(userId, filter);

        log.info("조회된 건수 : " + result.getReceiptList().size());
        result.getReceiptList().forEach(r -> log.info("receiptId = " + r.getReceiptId()));

        assertThat(result).isNotNull();
    }

    @DisplayName("이메일 DTO 반환 확인")
    @Test
    void sendEmail() {
        String email = "test@example.com";
        Long userId = 1L;

        EmailSendDto result = ceoService.sendEmail(email, userId);

        log.info("전송 이메일 : " + result.getEmail());
        assertThat(result.getEmail()).isEqualTo(email);
    }

    @DisplayName("상태 변경 정상 반영")
    @Test
    void receiptProcessing() {
        Long receiptProcessId = 1L;
        String state = "accepted";
        String reason = "";

        ReceiptProcessDto result = ceoService.receiptProcessing(receiptProcessId, state, reason);

        log.info("처리 상태 : " + result.getProcessStatus());
        assertThat(result.getProcessStatus()).isEqualTo(state);
    }

    @DisplayName("이번달 / 지난달 금액 계산")
    @Test
    void getCorporateCardTotalPrice() {
        Long userId = 1L;

        CorporateCardTotalPriceDto result = ceoService.getCorporateCardTotalPrice(userId);

        log.info("이번달 : " + result.getThisMonth());
        log.info("지난달 : " + result.getLastMonth());
        assertThat(result.getThisMonth()).isNotNull();
        assertThat(result.getLastMonth()).isNotNull();
    }

    @DisplayName("커서 기반 법카 내역 조회")
    @Test
    void getCorporateCardReceipts() {
        Long userId = 1L;
        ReceiptFilterDto filter = new ReceiptFilterDto();
        filter.setCursorId(Long.MAX_VALUE);
        filter.setSort(Sort.Newest);
        filter.setSize(10L);

        CorporateCardListCursorDto result = ceoService.getCorporateCardReceipts(userId, filter);

        log.info("조회된 법카 건수 : " + result.getCorporateCardList().size());
        result.getCorporateCardList().forEach(c -> log.info("receiptId = " + c.getReceiptId()));

        assertThat(result).isNotNull();
    }
}