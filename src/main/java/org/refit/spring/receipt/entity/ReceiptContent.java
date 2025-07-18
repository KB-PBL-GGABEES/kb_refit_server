package org.refit.spring.receipt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptContent {
    private Long receiptContentId;
    private Long receiptId;
    private Long merchandiseId;
    private Long amount;
    private Date createdAt;
}
