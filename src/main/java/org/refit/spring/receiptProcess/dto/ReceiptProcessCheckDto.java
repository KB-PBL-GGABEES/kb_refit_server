package org.refit.spring.receiptProcess.dto;

import lombok.*;

// 영수 처리 정보 조회
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptProcessCheckDto {
    private String companyName;
    private String address;
}
