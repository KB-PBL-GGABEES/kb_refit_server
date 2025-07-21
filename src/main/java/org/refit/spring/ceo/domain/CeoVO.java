package org.refit.spring.ceo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CeoVO {
    private Long receiptId;
    private String companyName; // 상호명
    private Long totalPrice;    // 결제금액
    private String receiptDate; // 결제일
    private String receiptTime; // 결제시간

//    private String storeImage;  // 상호이미지
}
