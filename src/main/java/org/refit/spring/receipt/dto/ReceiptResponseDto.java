package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.refit.spring.receipt.entity.Receipt;
import org.refit.spring.receipt.entity.ReceiptContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class ReceiptResponseDto {
    private Long userId;
    private Long receiptId;
    private List<ReceiptContentDto> contentsList;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private Long carbonPoint;
    private Long reward;
    private Date createdAt;

    public static ReceiptResponseDto from(Receipt receipt, Long userId) {
        return new ReceiptResponseDto(
                userId,
                receipt.getReceiptId(),
                receipt.getContentList(),
                receipt.getTotalPrice(),
                receipt.getSupplyPrice(),
                receipt.getSurtax(),
                100L,
                (long) (receipt.getTotalPrice() * 0.05),
                receipt.getCreatedAt()
        );
    }
}
