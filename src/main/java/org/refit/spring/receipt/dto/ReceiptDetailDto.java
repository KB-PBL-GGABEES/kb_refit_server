package org.refit.spring.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptDetailDto {
    private Long userId;
    private Long receiptId;
    private Long companyId;
    private String companyName;
    private String address;
    private List<ReceiptContentDto> receiptContents;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    private Date createdAt;
    private String processState;
}
