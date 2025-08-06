package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.*;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.mapper.CeoMapper;
import org.refit.spring.mapper.ReceiptMapper;
import org.refit.spring.receipt.dto.ReceiptContentDetailDto;
import org.refit.spring.ceo.dto.ReceiptDetailDto;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;
    private final ReceiptMapper receiptMapper;


    // 경비 처리가 필요한 내역 조회
    @Override
    public PendingDetailDto getPendingDetail(Long userId) {
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
        ReceiptDetailDto detail = ceoMapper.getReceiptDetailByReceiptId(receiptId);
        if (detail == null) throw new NoSuchElementException("영수증 없음");

        List<ReceiptContentDetailDto> contentList = ceoMapper.getReceiptContents(receiptId);
        detail.setReceiptContents(contentList);

        ReceiptProcessApplicantDto applicant = ceoMapper.getReceiptProcessDetail(receiptId);

        return new ReceiptProcessDetailDto(detail, applicant);
    }

    // 경비 처리 완료 내역 조회
    @Override
    public List<ReceiptDto> getCompletedReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, ProcessState processState, Sort sort) {
        if (cursorId == null) {
            cursorId = (sort == Sort.Oldest) ? 0L : Long.MAX_VALUE;
        }

        List<Ceo> result = ceoMapper.getCompletedReceipts(userId, cursorId, period, startDate, endDate, processState, sort);

        return result.stream()
                .map(ReceiptDto::of)
                .collect(Collectors.toList());
    }

    // 처리 완료된 항목 이메일 전송
    @Override
    public EmailSendDto sendEmail(String email, Long userId) {
        int numberSend = ceoMapper.countCompletedReceipts(userId);
        // 실제 이메일 전송 로직 생략
        return EmailSendDto.builder()
                .email(email)
//                .numberSend(numberSend)
                .build();
    }

    // 영수 처리 승인 및 반려
    @Override
    public ReceiptProcessDto receiptProcessing(Long receiptId, String progressState, String rejectedReason) {
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
    public List<CorporateCardListDto> getCorporateCardReceipts(Long userId, Long cursorId, Integer period, Date startDate, Date endDate, RejectState rejectState, Sort sort) {
        if (cursorId == null) {
            cursorId = (sort == Sort.Oldest) ? 0L : Long.MAX_VALUE;
        }

        List<CorporateCardListDto> result = ceoMapper.getCorporateCardReceipts(userId, cursorId, period, startDate, endDate, rejectState, sort);

        return result;
    }
}
