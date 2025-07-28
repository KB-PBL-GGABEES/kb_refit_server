package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetail {
    private Long userid;            // 사장님
    private String name;            // 사장님 이름
    private String progressType;    // 경비 처리 항목
    private String progressDetail;  // 세부 내용
    private Long receiptId;         // 영수증

    private Long totalPrice;        // 주문 합계
    private Long supplyPrice;       // 공급가금액
    private Long surtax;            // 부가세
    private String transactionType; // 거래 종류 (현금, 카드)

    private Long cardId;            // 카드
    private boolean isCorporate;    // 법인 카드 여부
    private String cardNumber;      // 카드 번호..
}
