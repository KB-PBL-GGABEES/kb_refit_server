package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.refit.spring.ceo.dto.*;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.mapper.CeoMapper;
import org.refit.spring.receipt.dto.ReceiptContentDetailDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;

    private void validateRequiredFields(Map<String, Object> fields) {
        List<String> missing = new ArrayList<>();

        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            Object value = entry.getValue();
            if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                missing.add(entry.getKey());
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("다음 필수 항목이 누락되었거나 비어 있습니다: " + String.join(", ", missing));
        }
    }

    // 경비 처리가 필요한 내역 조회
    @Override
    public PendingDetailDto getPendingDetail(Long userId) {
        validateRequiredFields(Collections.singletonMap("userId", userId));

        List<ReceiptDto> list = ceoMapper.getPendingReceipts(userId).stream().map(ReceiptDto::of).toList();
        int count = ceoMapper.countPendingReceipts(userId);
        int completed = ceoMapper.countCompletedReceiptsThisMonth(userId);
        return PendingDetailDto.builder()
                .userId(userId)
                .countPendingReceipts(count)
                .countCompletedReceiptsThisMonth(completed)
                .pendingReceipts(list)
                .build();
    }

    // 경비 청구 항목 상세 조회
    @Override
    public ReceiptProcessDetailDto getReceiptList(Long receiptId) {
        validateRequiredFields(Collections.singletonMap("receiptId", receiptId));

        ReceiptDetailDto detail = ceoMapper.getReceiptDetailByReceiptId(receiptId);
        if (detail == null) throw new NoSuchElementException("영수증 없음");

        List<ReceiptContentDetailDto> contentList = ceoMapper.getReceiptContents(receiptId);
        detail.setReceiptContents(contentList);

        ReceiptProcessApplicantDto applicant = ceoMapper.getReceiptProcessDetail(receiptId);

        return new ReceiptProcessDetailDto(detail, applicant);
    }

    // 경비 처리 완료 내역 조회
    @Override
    public ReceiptListCursorDto getCompletedReceipts(Long userId, ReceiptFilterDto receiptFilterDto) {
        validateRequiredFields(Collections.singletonMap("userId", userId));

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        long paginationSize = (receiptFilterDto.getSize() != null && receiptFilterDto.getSize() > 0) ? receiptFilterDto.getSize() : 20;

        // 기본 정렬값
        if (receiptFilterDto.getSort() == null) receiptFilterDto.setSort(Sort.Newest);

        // 커서 초기화
        if (receiptFilterDto.getCursorId() == null) {
            receiptFilterDto.setCursorId((receiptFilterDto.getSort() == Sort.Oldest) ? 0L : Long.MAX_VALUE);
        }

        // 날짜 필터
        if (receiptFilterDto.getPeriod() == null) {
            params.put("startDate", receiptFilterDto.getStartDate());
            params.put("endDate", receiptFilterDto.getEndDate());
        } else {
            params.put("period",  receiptFilterDto.getPeriod());
        }

        params.put("cursorId", receiptFilterDto.getCursorId());
        params.put("sort", receiptFilterDto.getSort());
        params.put("state", receiptFilterDto.getState());
        params.put("size", paginationSize);

        List<ReceiptDto> list = ceoMapper.getCompletedReceipts(params).stream().map(ReceiptDto::of).toList();

        // 커서 아이디 초기화
        Long nextCursorId = (list.size() < paginationSize) ?  null : list.get(list.size() - 1).getReceiptId();

        return ReceiptListCursorDto.from(list, nextCursorId);
    }

    @Override
    public int monthlySummary (Long userId) {
        validateRequiredFields(Collections.singletonMap("userId", userId));
        int numberSend = ceoMapper.countCompletedReceipts(userId);

        return numberSend;
    }

    // 처리 완료된 항목 이메일 전송
    @Override
    public EmailSendDto sendEmail(String email, Long userId) {
        validateRequiredFields(Collections.singletonMap("userId", userId));

        return EmailSendDto.builder()
                .email(email)
                .build();
    }

    // 영수 처리 승인 및 반려
    @Override
    public ReceiptProcessDto receiptProcessing(Long receiptId, String progressState, String rejectedReason) {
        validateRequiredFields(Collections.singletonMap("receiptId", receiptId));

        Long receiptProcessId = ceoMapper.getReceiptId(receiptId);
        if (receiptProcessId == null) {
            throw new IllegalArgumentException("영수 처리 내역이 존재하지 않습니다.");
        }

        ceoMapper.updateProcessState(receiptProcessId, progressState, rejectedReason);

        return ReceiptProcessDto.builder()
                .message("영수 처리 완료")
                .processStatus(progressState)
                .receiptId(receiptId)
                .build();
    }


    // 한달 법카 금액 조회
    @Override
    public CorporateCardTotalPriceDto getCorporateCardTotalPrice(Long userId) {
        validateRequiredFields(Collections.singletonMap("userId", userId));

        int month = LocalDateTime.now().getMonthValue();
        Long thisMonth = ceoMapper.getCorporateCardCostThisMonth(userId);
        Long lastMonth = ceoMapper.getCorporateCardCostLastMonth(userId);

        return CorporateCardTotalPriceDto.builder()
                .month(month)
                .thisMonth(thisMonth != null ? thisMonth : 0L)
                .lastMonth(lastMonth != null ? lastMonth : 0L)
                .build();
    }

    // 법카 내역 조회
    @Override
    public CorporateCardListCursorDto getCorporateCardReceipts(Long userId, ReceiptFilterDto receiptFilterDto) {
        validateRequiredFields(Collections.singletonMap("userId", userId));

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        long paginationSize = (receiptFilterDto.getSize() != null && receiptFilterDto.getSize() > 0) ? receiptFilterDto.getSize() : 20;

        // 기본 정렬값
        if (receiptFilterDto.getSort() == null) receiptFilterDto.setSort(Sort.Newest);

        // 커서 초기화
        if (receiptFilterDto.getCursorId() == null) {
            receiptFilterDto.setCursorId((receiptFilterDto.getSort() == Sort.Oldest) ? 0L : Long.MAX_VALUE);
        }

        // 날짜 필터
        if (receiptFilterDto.getPeriod() == null) {
            params.put("startDate", receiptFilterDto.getStartDate());
            params.put("endDate", receiptFilterDto.getEndDate());
        } else {
            params.put("period",  receiptFilterDto.getPeriod());
        }

        params.put("cursorId", receiptFilterDto.getCursorId());
        params.put("sort", receiptFilterDto.getSort());
        params.put("state", receiptFilterDto.getState());
        params.put("size", paginationSize);

        List<CorporateCardDto> list = ceoMapper.getCorporateCardReceipts(params);

        // 커서 아이디 초기화
        Long nextCursorId = (list.size() < paginationSize) ?  null : list.get(list.size() - 1).getReceiptId();

        return CorporateCardListCursorDto.from(list, nextCursorId);
    }
}
