package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.refit.spring.ceo.dto.CeoListDto;
import org.refit.spring.ceo.dto.CorporateCardListDto;
import org.refit.spring.ceo.dto.ReceiptListDto;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.mapper.CeoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    public ReceiptListDto getReceiptList(Long receipted, Long userId) {
        return ceoMapper.getReceiptList(receipted, userId);
    }

    // 경비 처리 완료 내역 조회
    @Override
    public List<CeoListDto> getCompletedReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, ProcessState processState, Sort sort) {
        if (cursorId == null) {
            cursorId = (sort == Sort.Oldest) ? 0L : Long.MAX_VALUE;
        }

        List<Ceo> result = ceoMapper.getCompletedReceipts(userId, cursorId, period, startDate, endDate, processState, sort);

        return result.stream()
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
    @Override
    public Map<String, Object> getCorporateCardCost(Long userId) {
        Long thisMonth = ceoMapper.getCorporateCardCostThisMonth(userId);
        Long lastMonth = ceoMapper.getCorporateCardCostLastMonth(userId);

        return Map.of(
                "month", LocalDateTime.now().getMonthValue(),
                "totalPrice", thisMonth != null ? thisMonth : 0L,
                "lastMonth", lastMonth != null ? lastMonth : 0L
        );
    }

    // 법카 내역 조회
    @Override
    public List<CorporateCardListDto> getCorporateCardReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, RejectState rejectState, Sort sort) {
        if (cursorId == null) {
            cursorId = (sort == Sort.Oldest) ? 0L : Long.MAX_VALUE;
        }

        List<CorporateCardListDto> result = ceoMapper.getCorporateCardReceipts(userId, cursorId, period, startDate, endDate, rejectState, sort);

        return result;
    }
}
