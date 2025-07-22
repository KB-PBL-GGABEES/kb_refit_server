package org.refit.spring.receipt.dto;

import lombok.Data;

@Data
public class ReceiptContentDto {
    private Long merchandiseId;
    private String merchandiseName;
    private Long merchandisePrice;
    private Long amount;
}
