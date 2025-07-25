package org.refit.spring.receipt.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.MerchandiseMapper;
import org.refit.spring.mapper.ReceiptMapper;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.receipt.dto.*;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptMapper receiptMapper;
    private final MerchandiseMapper merchandiseMapper;

    @Transactional
    public Receipt create(ReceiptRequestDto dto, Long userId) {
        List<ReceiptContentRequestsDto> requestList = dto.getContentsList();
        Merchandise firstMerchandise = merchandiseMapper.findByMerchandiseId(requestList.get(0).getMerchandiseId());
        Receipt receipt = initReceipt(firstMerchandise.getCompanyId(), userId);
        List<ReceiptContentDto> list = makeContents(dto.getContentsList(), receipt);
        updatePrice(receipt);
        receipt.setContentList(list);
        receiptMapper.update(receipt);
        return receipt;
    }

    @Transactional
    public Receipt refund(Long userId, Long receiptId) {
        Receipt nowReceipt = receiptMapper.get(receiptId);
        if (nowReceipt == null) throw new IllegalArgumentException("존재하지 않는 영수증입니다.");
        Receipt refundReceipt = new Receipt();
        refundReceipt.setTotalPrice(-nowReceipt.getTotalPrice());
        refundReceipt.setSupplyPrice(-nowReceipt.getSupplyPrice());
        refundReceipt.setSurtax(-nowReceipt.getSurtax());
        refundReceipt.setTransactionType("환불");
        refundReceipt.setCreatedAt(new Date());
        refundReceipt.setCompanyId(nowReceipt.getCompanyId());
        refundReceipt.setUserId(userId);
        receiptMapper.create(refundReceipt);

        return refundReceipt;
    }

    private Receipt initReceipt(Long companyId, Long userId) {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(0L);
        receipt.setSupplyPrice(0L);
        receipt.setSurtax(0L);
        receipt.setTransactionType("카드 결제");
        receipt.setCreatedAt(new Date());
        receipt.setUserId(userId);
        receipt.setCompanyId(companyId);
        receiptMapper.create(receipt);
        return receipt;
    }

    private List<ReceiptContentDto> makeContents(List<ReceiptContentRequestsDto> list, Receipt receipt) {
        List<ReceiptContentDto> contentDtoList = new ArrayList<>();
        for (ReceiptContentRequestsDto requestsDto: list) {
            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(requestsDto.getMerchandiseId());
            ReceiptContent content = makeContent(requestsDto, merchandise, receipt.getReceiptId());
            receiptMapper.createReceiptContent(content);

            contentDtoList.add(makeMerchandise(merchandise, requestsDto));
            receipt.setTotalPrice(receipt.getTotalPrice() + merchandise.getMerchandisePrice() * requestsDto.getAmount());
        }
        return contentDtoList;
    }

    private ReceiptContent makeContent(ReceiptContentRequestsDto dto, Merchandise merchandise, Long receiptId) {
        ReceiptContent content = new ReceiptContent();
        content.setAmount(dto.getAmount());
        content.setReceiptId(receiptId);
        content.setCreatedAt(new Date());
        content.setMerchandiseId(merchandise.getMerchandiseId());
        return content;
    }

    private ReceiptContentDto makeMerchandise(Merchandise merchandise, ReceiptContentRequestsDto dto) {
        ReceiptContentDto result = new ReceiptContentDto();
        result.setMerchandiseId(merchandise.getMerchandiseId());
        result.setMerchandiseName(merchandise.getMerchandiseName());
        result.setMerchandisePrice(merchandise.getMerchandisePrice());
        result.setAmount(dto.getAmount());
        return result;
    }

    private void updatePrice(Receipt receipt) {
        long total = receipt.getTotalPrice();
        long supply = (long) (total / 1.1);
        receipt.setSupplyPrice(supply);
        receipt.setSurtax(total - supply);
    }

    @Transactional(readOnly = true)
    public ReceiptListDto getList(Long userId, Long cursorId) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;
        List<Receipt> receipts = receiptMapper.getList(cursorId);
        Long nextCursorId = receipts.size() < 20 ? null : receipts.get(receipts.size() - 1).getReceiptId();
        return ReceiptListDto.from(userId, receipts, nextCursorId);
    }

    @Transactional(readOnly = true)
    public Receipt get(Long cursorId, Long receiptId) {
        if (cursorId == null) cursorId = Long.MAX_VALUE;

        Receipt receipt = receiptMapper.get(receiptId);
        if (receipt == null) {
            throw new NoSuchElementException();
        }
        List<ReceiptContent> contents = receiptMapper.findContentsByReceiptId(receiptId);
        List<ReceiptContentDto> contentDtoList = new ArrayList<>();
        for (ReceiptContent content: contents) {
            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(content.getMerchandiseId());
            ReceiptContentDto dto = new ReceiptContentDto();
            dto.setMerchandiseId(merchandise.getMerchandiseId());
            dto.setMerchandiseName(merchandise.getMerchandiseName());
            dto.setMerchandisePrice(merchandise.getMerchandisePrice());
            dto.setAmount(content.getAmount());
            contentDtoList.add(dto);
        }
        receipt.setContentList(contentDtoList);
        return receipt;
    }


    @Transactional
    public ReceiptTotalDto getTotal(Long userId) {
        ReceiptTotalDto dto = new ReceiptTotalDto();
        dto.setUserId(userId);
        dto.setTotal(receiptMapper.getTotal());
        dto.setLastMonth(receiptMapper.getLastMonthTotal());
        return dto;
    }
}
