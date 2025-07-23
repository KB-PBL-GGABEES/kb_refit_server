package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptContentResponseDto {
    private Long merchandiseId;
    private String merchandiseName;
    private Long merchandisePrice;
    private Long amount;
}
