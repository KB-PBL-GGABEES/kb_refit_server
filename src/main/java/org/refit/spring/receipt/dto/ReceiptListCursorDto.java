package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.refit.spring.receipt.entity.Receipt;

import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptListCursorDto {
    private List<Receipt> receiptList;
    private Long nextCursorId;

    public static ReceiptListCursorDto from(List<Receipt> list, Long nextCursorId) {
        return new ReceiptListCursorDto(list, nextCursorId);
    }
}
