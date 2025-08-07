package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptListCursorDto {
    private List<ReceiptDto> receiptList;
    private Long cursorId;

    public static ReceiptListCursorDto from(List<ReceiptDto> list, Long nextCursorId) {
        return new ReceiptListCursorDto(list, nextCursorId);
    }
}
