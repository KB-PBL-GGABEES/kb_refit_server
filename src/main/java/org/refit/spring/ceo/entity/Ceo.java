package org.refit.spring.ceo.entity;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ceo {
    private Long receiptId;
    private String companyName; // 상호명
    private Long totalPrice;    // 결제금액
    private Date createdAt; // 결제 일시
    private String processState; // 영수처리 여부
}
