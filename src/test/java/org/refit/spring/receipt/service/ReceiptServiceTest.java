package org.refit.spring.receipt.service;

import lombok.extern.log4j.Log4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.refit.spring.config.RootConfig;
import org.refit.spring.receipt.dto.*;
import org.refit.spring.receipt.entity.Receipt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;

import java.text.ParseException;
import java.util.List;

@SpringJUnitWebConfig(classes = {RootConfig.class})
@Log4j
class ReceiptServiceTest {

    @Autowired
    private ReceiptService service;


    @DisplayName("구매 영수증 생성 테스트")
    @Test
    void create() throws ParseException {
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
        Long userId = 5L;
        ReceiptListRequestDto requestDto = new ReceiptListRequestDto();
        requestDto.setSize(10L);
        requestDto.setSort(null);
        requestDto.setFilter(null);
        requestDto.setPeriod(1);
        requestDto.setStartDate(null);
        requestDto.setEndDate(null);
        requestDto.setType(null);
        requestDto.setCursorId(null);
        ReceiptListCursorDto listDto = service.getFilteredList(userId, requestDto);
        log.info(listDto.getReceiptList());
        log.info(listDto.getNextCursorId());
    }

    @DisplayName("구매 영수증 상세 조회 테스트")
    @Test
    void get() {
        Long receiptId = 8L;
        Long cursorId = null;
        ReceiptDetailDto receipt = service.get(5L, cursorId, receiptId);
        log.info(receipt.getReceiptId());
        log.info(receipt.getTotalPrice());
        for (ReceiptContentDetailDto contentDto: receipt.getReceiptContents()) {
            log.info(contentDto.getMerchandiseName());
            log.info(contentDto.getMerchandisePrice());
            log.info(contentDto.getAmount());
        }
    }

    @DisplayName("이번 달, 저번 달 총 사용 금액 조회 테스트")
    @Test
    void getTotal() {
        Long userId = 1L;
        MonthlyExpenseDto dto = service.getTotal(userId);
        log.info(dto.getLastMonthExpense());
        log.info(dto.getThisMonthExpense());
    }

    @DisplayName("영수증 환불 테스트")
    @Test
    void refund() throws ParseException {
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
        Long userId = 5L;
        RejectedReceiptListDto result = service.getRejected(userId);
        log.info(result.getRejectedList());
    }

    @DisplayName("배지 조건 확인 후 획득 처리")
    @Test
    void checkAndInsertBadge() {
        try {
            service.checkAndInsertBadge(5L, 78L);
            log.info("성공");
        }
        catch (Exception e) {
            log.info("실패");
        }
    }
}