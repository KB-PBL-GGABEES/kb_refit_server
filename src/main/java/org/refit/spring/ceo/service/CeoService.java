package org.refit.spring.ceo.service;

import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;

import java.util.List;

public interface CeoService {

    // 경비 처리가 필요한 내역 조회
    List<CeoListDto> getPendingReceipts(Long userId);
    int countPendingReceipts(Long userId);
    int countCompletedReceiptsThisMonth(Long userId);

    // 경비 청구 항목 상세 조회
    ReceiptDetailDto getReceiptDetail(Long receipted, Long userId);

    // 경비 처리 완료 내역 조회
    List<CeoListDto> getCompletedReceipts(int period, String cursorDateTime, Long userId);

    // 처리 완료된 항목 이메일 전송
    int countCompletedReceipts();
    void sendEmail(String email, Long userId);

    // 영수 처리 승인 및 반려
    void receiptProcessing(Long receiptProcessId, String progressState, String rejectedReason, Long userId);
    
    // 한달 법카 금액 조회
    
    // 법카 내역 조회
}
