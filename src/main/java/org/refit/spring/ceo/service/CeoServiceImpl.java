package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.mapper.CeoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;

    // 경비 처리가 필요한 내역 조회
    @Override
    public List<CeoListDto> getPendingReceipts(Long userId) {

        return ceoMapper.getPendingReceipts(userId)
                .stream()
                .map(CeoListDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public int countPendingReceipts(Long userId) {
        return ceoMapper.countPendingReceipts(userId);
    }

    @Override
    public int countCompletedReceiptsThisMonth(Long userId) {
        return ceoMapper.countCompletedReceiptsThisMonth(userId);
    }

    // 경비 청구 항목 상세 조회
    @Override
    public ReceiptDetailDto getReceiptDetail(Long receipted, Long userId) {
        return ceoMapper.getReceiptDetail(receipted, userId);
    }

    // 경비 처리 완료 내역 조회
    @Override
    public List<CeoListDto> getCompletedReceipts(int period, String cursorDateTime, Long userId) {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(period);
        LocalDateTime cursor = (cursorDateTime == null)
                ? LocalDateTime.now().plusDays(1)
                : LocalDateTime.parse(cursorDateTime);

        return ceoMapper.getCompletedReceipts(fromDate, cursor, userId)
                .stream()
                .map(CeoListDto::of)
                .collect(Collectors.toList());
    }

    // 처리 완료된 항목 이메일 전송
    @Override
    public int countCompletedReceipts(Long userId) {
        return ceoMapper.countCompletedReceipts(userId);
    }

    @Override
    public void sendEmail(String email, Long userId) {
        // 추후 실제 전송하는 로직 구현
    }

    // 영수 처리 승인 및 반려
    @Override
    public void receiptProcessing(Long receiptProcessId, String progressState, String rejectedReason, Long userId) {
        ceoMapper.updateProcessState(receiptProcessId, progressState, rejectedReason, userId);
    }

    // 한달 법카 금액 조회

    // 법카 내역 조회
}
