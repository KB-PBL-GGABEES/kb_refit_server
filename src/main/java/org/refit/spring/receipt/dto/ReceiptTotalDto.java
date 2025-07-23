package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptTotalDto {
    private Long userId;
    private Long total;
    private Long lastMonth;
}
