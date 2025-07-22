package org.refit.spring.ceo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailVO {
    private Long userid;            // 사장님
    private String name;            // 사장님 이름
    private String progressType;    // 경비 처리 항목
    private String progressDetail;  // 세부 내용
    private Long receiptId;         // 영수증
}
