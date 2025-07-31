package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.Ceo;
import org.refit.spring.ceo.entity.ReceiptProcess;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptCompletedListDto {
    private Long userId;
    private Ceo ceo;
    private List<ReceiptProcess> receiptProcessList;
    private Long cursorId;

    public static ReceiptCompletedListDto from(Long userId, Ceo ceo, List<ReceiptProcess> eceiptProcessList, Long cursorId) {
        return new ReceiptCompletedListDto(
                userId, ceo, eceiptProcessList, cursorId
        );
    }
}
