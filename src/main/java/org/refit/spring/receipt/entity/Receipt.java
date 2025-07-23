package org.refit.spring.receipt.entity;

import lombok.*;
import org.refit.spring.receipt.dto.ReceiptContentDto;

import java.util.Date;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Receipt {
    private Long receiptId;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    private boolean isRefund;
    private Date createdAt;
    private Date updatedAt;
    List<ReceiptContentDto> contentList;
}
