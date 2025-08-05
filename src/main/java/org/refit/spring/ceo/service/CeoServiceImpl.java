package org.refit.spring.ceo.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.ceo.dto.*;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.enums.ProcessState;
import org.refit.spring.ceo.enums.RejectState;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.mapper.CeoMapper;
import org.refit.spring.mapper.MerchandiseMapper;
import org.refit.spring.mapper.ReceiptMapper;
import org.refit.spring.mapper.ReceiptProcessMapper;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.receipt.dto.ReceiptContentDetailDto;
import org.refit.spring.receipt.dto.ReceiptDetailDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CeoServiceImpl implements CeoService {
    private final CeoMapper ceoMapper;
    private final ReceiptMapper receiptMapper;
    private final MerchandiseMapper merchandiseMapper;
    private final ReceiptProcessMapper receiptProcessMapper;

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
    public ReceiptProcessDetailDto getReceiptList(Long userId, Long receiptId) {
        // 영수증 조회
        Receipt receipt = receiptMapper.get(userId, receiptId);
        if (receipt == null) throw new NoSuchElementException("영수증 없음");

        List<ReceiptContent> contents = receiptMapper.findContentsByReceiptId(userId, receiptId);
        List<ReceiptContentDetailDto> contentDtoList = new ArrayList<>();
        for (ReceiptContent content: contents) {
            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(content.getMerchandiseId());
            ReceiptContentDetailDto dto = new ReceiptContentDetailDto();
            dto.setMerchandiseId(merchandise.getMerchandiseId());
            dto.setMerchandiseName(merchandise.getMerchandiseName());
            dto.setMerchandisePrice(merchandise.getMerchandisePrice());
            dto.setAmount(content.getAmount());
            contentDtoList.add(dto);
        }

        ReceiptDetailDto detail = new ReceiptDetailDto(
                receipt.getUserId(),
                receipt.getReceiptId(),
                receipt.getCompanyId(),
                receiptMapper.getCompanyName(receipt.getCompanyId()),
                receiptMapper.findCeoName(receipt.getCompanyId()),
                receiptMapper.getCompanyAddress(receipt.getCompanyId()),
                contentDtoList,
                receipt.getTotalPrice(),
                receipt.getSupplyPrice(),
                receipt.getSurtax(),
                receipt.getTransactionType(),
                receipt.getCreatedAt(),
                Optional.ofNullable(receiptMapper.getState(receiptId)).orElse("none"),
                receiptMapper.getCardNumber(userId, receipt.getCardId()),
                Optional.ofNullable(receiptMapper.getCorporate(userId, receipt.getCardId())).orElse(0),
                Optional.ofNullable(receiptProcessMapper.findReason(receiptId)).orElse(""));

        detail.setUserId(receipt.getUserId());
        detail.setReceiptId(receipt.getReceiptId());
        detail.setCompanyId(receipt.getCompanyId());
        detail.setCompanyName(receiptMapper.getCompanyName(receipt.getCompanyId()));
        detail.setCeoName(receiptMapper.findCeoName(receipt.getCompanyId()));
        detail.setAddress(receiptMapper.getCompanyAddress(receipt.getCompanyId()));
        detail.setReceiptContents(contentDtoList);
        detail.setTotalPrice(receipt.getTotalPrice());
        detail.setSupplyPrice(receipt.getSupplyPrice());
        detail.setSurtax(receipt.getSurtax());
        detail.setTransactionType(receipt.getTransactionType());
        detail.setCreatedAt(receipt.getCreatedAt());
        detail.setProcessState(receiptMapper.getState(receiptId));
        detail.setCardNumber(receiptMapper.getCardNumber(userId, receipt.getCardId()));
        detail.setIsCorporate(receiptMapper.getCorporate(userId, receipt.getCardId()));
        detail.setRejectedReason(receiptProcessMapper.findReason(receiptId));

        // 중복 조회 예외 처리
        ReceiptProcessApplicantDto info;
        try {
            info = ceoMapper.getReceiptProcessDetail(receiptId, userId);
        } catch (org.apache.ibatis.exceptions.TooManyResultsException e) {
            throw new IllegalStateException("중복된 영수 처리 정보가 존재합니다. DB 무결성을 확인해주세요.");
        }
        return new ReceiptProcessDetailDto(detail, info);
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
                .numberSend(numberSend)
                .build();
    }

    // 영수 처리 승인 및 반려
    @Override
    public ReceiptProcessDto receiptProcessing(Long receiptProcessId, String progressState, String rejectedReason, Long userId) {
        ceoMapper.updateProcessState(receiptProcessId, progressState, rejectedReason, userId);
        return ReceiptProcessDto.builder()
                .message("영수 처리 완료")
                .processStatus(progressState)
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
