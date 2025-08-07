package org.refit.spring.ceo.service;

import org.refit.spring.ceo.dto.*;

public interface CeoService {

    // 경비 처리가 필요한 내역 조회
    PendingDetailDto getPendingDetail(Long userId);

    // 경비 청구 항목 상세 조회
    ReceiptProcessDetailDto getReceiptList(Long receipted);

    // 경비 처리 완료 내역 조회
    ReceiptListCursorDto getCompletedReceipts(Long userId, ReceiptFilterDto receiptFilterDto);

    int monthlySummary(Long userId);

    // 처리 완료된 항목 이메일 전송
    EmailSendDto sendEmail(String email, Long userId);

    // 영수 처리 승인 및 반려
    ReceiptProcessDto receiptProcessing(Long receiptId, String progressState, String rejectedReason);

    // 한달 법카 금액 조회
    CorporateCardTotalPriceDto getCorporateCardTotalPrice(Long userId);

    // 법카 내역 조회
    CorporateCardListCursorDto getCorporateCardReceipts(Long userId, ReceiptFilterDto receiptFilterDto);
}
