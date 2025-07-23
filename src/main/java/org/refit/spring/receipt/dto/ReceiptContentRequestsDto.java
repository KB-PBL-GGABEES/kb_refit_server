package org.refit.spring.receipt.dto;

import lombok.Data;

@Data
public class ReceiptContentRequestsDto {
    private Long merchandiseId;
    private Long amount;
}
