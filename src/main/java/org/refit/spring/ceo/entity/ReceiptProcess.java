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
    private String documentType;
    private String documentDetail;
    private Date createdAt;
    private Date updatedAt;
    private String rejectedReason;
    private String imageFileName;
    private Long receiptId;
}
