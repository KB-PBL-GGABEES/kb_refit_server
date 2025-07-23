package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.refit.spring.receipt.entity.Receipt;

import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptListDto {
    private Long userId;
    private List<Receipt> receiptList;
    private Long nextCursorId;

    public static ReceiptListDto from(List<Receipt> receipt, Long userId, Long nextCursorId) {
        return new ReceiptListDto(
                userId,
                receipt,
                nextCursorId
        );
    }
}
