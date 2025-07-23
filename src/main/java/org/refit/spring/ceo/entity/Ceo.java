package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ceo {
    private Long receiptId;
    private String companyName; // 상호명
    private Long totalPrice;    // 결제금액
    private LocalDateTime receiptDateTime; // 결제 일시
    private String processState; // 영수처리 여부

//    private String storeImage;  // 상호이미지
}
