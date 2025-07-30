package org.refit.spring.receipt.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.refit.spring.config.RootConfig;
import org.refit.spring.receipt.dto.*;
import org.refit.spring.receipt.entity.Receipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RootConfig.class})
@Log4j
class ReceiptServiceTest {

    @Autowired
    private ReceiptService service;


    @DisplayName("구매 영수증 생성 테스트")
    @Test
    void create() {
        ReceiptRequestDto dto = new ReceiptRequestDto();
        ReceiptContentRequestsDto content = new ReceiptContentRequestsDto();
        content.setMerchandiseId(1L);
        content.setAmount(3L);
        dto.setCardId(1L);
        dto.setContentsList(List.of(content));

        Receipt receipt = service.create(dto, 1L);
        log.info(receipt);
    }

    @DisplayName("구매 영수증 목록 조회 테스트")
    @Test
    void getList() {
        Long userId = 1L;
        Long cursorId = null;
        ReceiptListDto listDto = service.getList(userId, cursorId);
        log.info(listDto.getReceiptList());
        log.info(listDto.getNextCursorId());
    }

    @DisplayName("구매 영수증 상세 조회 테스트")
    @Test
    void get() {
        Long receiptId = 8L;
        Long cursorId = null;
        ReceiptDetailDto receipt = service.get(1L, cursorId, receiptId);
        log.info(receipt.getReceiptId());
        log.info(receipt.getTotalPrice());
        for (ReceiptContentDto contentDto: receipt.getReceiptContents()) {
            log.info(contentDto.getMerchandiseName());
            log.info(contentDto.getMerchandisePrice());
            log.info(contentDto.getAmount());
        }
    }

    @DisplayName("이번 달, 저번 달 총 사용 금액 조회 테스트")
    @Test
    void getTotal() {
        Long userId = 1L;
        ReceiptTotalDto dto = service.getTotal(userId);
        log.info(dto.getTotal());
        log.info(dto.getLastMonth());
    }

    @DisplayName("영수증 환불 테스트")
    @Test
    void refund() {
        ReceiptRequestDto dto = new ReceiptRequestDto();
        ReceiptContentRequestsDto content = new ReceiptContentRequestsDto();
        content.setMerchandiseId(1L);
        content.setAmount(1L);
        dto.setContentsList(List.of(content));
        dto.setCardId(1L);

        Receipt original = service.create(dto, 1L);
        Receipt refund = service.refund(1L, original.getReceiptId());

        log.info(refund);
    }

    @DisplayName("처리 대상 영수증 상태 변경")
    @Test
    void changeState() {
        Long userId = 1L;
        Long receiptProcessId = 1L;
        try {
            service.changeState(userId, receiptProcessId);
            log.info("성공");
        } catch (Exception e) {
            log.warn("실패");
        }
    }

    @DisplayName("처리 거절된 영수증 목록 조회")
    @Test
    void getRejected() {
        Long userId = 1L;
        RejectedListDto result = service.getRejected(userId);
        log.info(result.getList());
    }
}