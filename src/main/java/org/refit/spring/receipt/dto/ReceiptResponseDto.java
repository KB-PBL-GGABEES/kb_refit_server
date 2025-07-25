package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.refit.spring.receipt.entity.Receipt;

import java.text.SimpleDateFormat;
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
    private String createdAt;

    public static ReceiptResponseDto from(Receipt receipt, Long userId, Long carbon, Long price) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return new ReceiptResponseDto(
                userId,
                receipt.getReceiptId(),
                receipt.getContentList(),
                receipt.getTotalPrice(),
                receipt.getSupplyPrice(),
                receipt.getSurtax(),
                carbon,
                price,
                sdf.format(receipt.getCreatedAt())
        );
    }
}
