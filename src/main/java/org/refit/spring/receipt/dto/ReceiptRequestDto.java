package org.refit.spring.receipt.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReceiptRequestDto {
    private Long carbonPoint;
    private Long reward;

    List<ReceiptContentRequestsDto> contentsList;
}
