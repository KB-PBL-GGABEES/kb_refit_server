package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {
    private Long receiptId;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    private Date createdAt;
    private Date updatedAt;
    private Long companyId;
    private Long userId;
    private Long cardId;
}
