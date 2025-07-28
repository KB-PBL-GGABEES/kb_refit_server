package org.refit.spring.receipt.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReceiptRequestDto {
    private Long cardId;
    List<ReceiptContentRequestsDto> contentsList;
}
