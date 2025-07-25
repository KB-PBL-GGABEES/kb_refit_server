package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.mapper.CeoMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;

    // 경비 처리가 필요한 내역 조회
    @Override
    public List<CeoListDto> getListUndone(Long cursorId) {
        if(cursorId == null) { cursorId = Long.MAX_VALUE; }

        return ceoMapper.getListUndone(cursorId)
                .stream()
                .map(CeoListDto::of)
                .collect(Collectors.toList());
    }

    // 경비 청구 항목 상세 조회
    @Override
    public ReceiptDetailDto getReceiptDetail(Long receipted) {
        return ceoMapper.getReceiptDetail(receipted);
    }

    // 경비 처리 완료 내역 조회
    @Override
    public List<CeoListDto> getListDone(int period, String cursorDateTime) {
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(period);
        LocalDateTime cursor = (cursorDateTime == null)
                ? LocalDateTime.now().plusDays(1)
                : LocalDateTime.parse(cursorDateTime);

        return ceoMapper.getListDone(fromDate, cursor)
                .stream()
                .map(CeoListDto::of)
                .collect(Collectors.toList());
    }

    // 처리 완료된 항목 이메일 전송
    @Override
    public int countDoneReceipt() {
        return ceoMapper.countDone();
    }

    @Override
    public void sendEmail(String email) {
        // 추후 실제 전송하는 로직 구현
    }

    // 영수 처리 승인 및 반려
    @Override
    public void processReceipt(Long receiptProcessId, String progressState, String rejectedReason) {
        ceoMapper.updateProcessState(receiptProcessId, progressState, rejectedReason);
    }

    // 한달 법카 금액 조회

    // 법카 내역 조회
}
