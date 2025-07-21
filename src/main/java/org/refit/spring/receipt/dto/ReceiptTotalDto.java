package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptTotalDto {
    private Long userId;
    private Long total;
}
