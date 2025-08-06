package org.refit.spring.ceo.service;

import org.refit.spring.ceo.dto.*;
import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;

import java.util.Date;
import java.util.List;

public interface CeoService {

    // 경비 처리가 필요한 내역 조회
    PendingDetailDto getPendingDetail(Long userId);

    // 경비 청구 항목 상세 조회
    ReceiptProcessDetailDto getReceiptList(Long receipted);

    // 경비 처리 완료 내역 조회
    List<ReceiptDto> getCompletedReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, ProcessState processState, Sort sort);

    int monthlySummary(Long userId);

    // 처리 완료된 항목 이메일 전송
    EmailSendDto sendEmail(String email, Long userId);

    // 영수 처리 승인 및 반려
    ReceiptProcessDto receiptProcessing(Long receiptId, String progressState, String rejectedReason);

    // 한달 법카 금액 조회
    CorporateCardTotalPriceDto getCorporateCardTotalPrice(Long userId);

    // 법카 내역 조회
    List<CorporateCardListDto> getCorporateCardReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, RejectState rejectState, Sort sort);
}
