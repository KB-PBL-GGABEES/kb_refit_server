package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateCardList {
    private Long receiptId;         // 영수증
    private String companyName;     // 상호명
    private Long totalPrice;        // 주문 합계
    private String createdAt; // 결제 일시
    private String processState;    // 영수처리 여부
    private boolean corporate;      // 법인 카드 여부
}
