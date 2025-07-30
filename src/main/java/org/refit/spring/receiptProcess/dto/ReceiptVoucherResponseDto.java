package org.refit.spring.receiptProcess.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReceiptVoucherResponseDto {
    private Long receiptId;
    private String voucher;
}
