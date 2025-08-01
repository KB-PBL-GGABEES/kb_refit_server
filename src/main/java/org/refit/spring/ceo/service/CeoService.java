package org.refit.spring.ceo.service;

import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.CorporateCardListDto;
import org.refit.spring.ceo.dto.ReceiptListDto;
import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CeoService {

    // 경비 처리가 필요한 내역 조회
    List<CeoListDto> getPendingReceipts(Long userId);
    int countPendingReceipts(Long userId);
    int countCompletedReceiptsThisMonth(Long userId);

    // 경비 청구 항목 상세 조회
    ReceiptListDto getReceiptList(Long receipted, Long userId);

    // 경비 처리 완료 내역 조회
    List<CeoListDto> getCompletedReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, ProcessState processState, Sort sort);

    // 처리 완료된 항목 이메일 전송
    int countCompletedReceipts(Long userId);
    void sendEmail(String email, Long userId);

    // 영수 처리 승인 및 반려
    void receiptProcessing(Long receiptProcessId, String progressState, String rejectedReason, Long userId);

    // 한달 법카 금액 조회
    Map<String, Object> getCorporateCardCost(Long userId);

    // 법카 내역 조회
    List<CorporateCardListDto> getCorporateCardReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, RejectState rejectState, Sort sort);
}
