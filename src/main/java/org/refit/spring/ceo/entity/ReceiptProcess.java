package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptProcess {
    private Long receiptProcessId;
    private String processState;
    private Long ceoId;
    private String progressType;
    private String progressDetail;
    private Date createdAt;
    private String rejectedReason;
    private String voucher;
    private Long receiptId;
}
