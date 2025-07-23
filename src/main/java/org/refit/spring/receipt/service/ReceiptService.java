package org.refit.spring.receipt.service;

import lombok.RequiredArgsConstructor;
import org.refit.spring.mapper.MerchandiseMapper;
import org.refit.spring.mapper.ReceiptMapper;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.receipt.dto.ReceiptContentDto;
import org.refit.spring.receipt.dto.ReceiptContentRequestsDto;
import org.refit.spring.receipt.dto.ReceiptRequestDto;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptMapper receiptMapper;
    private final MerchandiseMapper merchandiseMapper;

    public Receipt create(ReceiptRequestDto dto) {
        Receipt receipt = new Receipt();
        receipt.setTotalPrice(0L);
        receipt.setSupplyPrice(0L);
        receipt.setSurtax(0L);
        receipt.setTransactionType("카드 결제");
        receipt.setRefund(false);
        receipt.setCreatedAt(new Date());
        receiptMapper.create(receipt);
        Long total = 0L;
        List<ReceiptContentDto> list = new ArrayList<>();
        for (ReceiptContentRequestsDto contentDto: dto.getContentsList()) {
            Merchandise merchandise = merchandiseMapper.findByMerchandiseId(contentDto.getMerchandiseId());
            ReceiptContent content = new ReceiptContent();
            ReceiptContentDto contentDetailDto = new ReceiptContentDto();
            contentDetailDto.setMerchandiseId(merchandise.getMerchandiseId());
            contentDetailDto.setMerchandiseName(merchandise.getMerchandiseName());
            contentDetailDto.setMerchandisePrice(merchandise.getMerchandisePrice());
            contentDetailDto.setAmount(contentDto.getAmount());
            content.setAmount(contentDto.getAmount());
            content.setReceiptId(receipt.getReceiptId());
            content.setCreatedAt(new Date());
            content.setMerchandiseId(merchandise.getMerchandiseId());
            total += merchandise.getMerchandisePrice() * contentDto.getAmount();
            receiptMapper.createReceiptContent(content);
            list.add(contentDetailDto);
        }
        receipt.setTotalPrice(total);
        receipt.setSupplyPrice((long) (total / 1.1));
        receipt.setSurtax(total - receipt.getSupplyPrice());
        receipt.setContentList(list);
        receiptMapper.update(receipt);
        return receipt;
    }
}
