package org.refit.spring.receiptProcess.dto;

import lombok.Getter;
import lombok.Setter;

// 영수 처리 요청
@Getter
@Setter
public class ReceiptProcessRequestDto {
    private Long ceoId;
    private Long companyId;
    private Long receiptId;
    private String progressType;
    private String progressDetail;
    private String fileName;
}
