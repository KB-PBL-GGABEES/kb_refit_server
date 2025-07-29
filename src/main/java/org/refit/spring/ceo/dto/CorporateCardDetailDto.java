package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.CorporateCardDetail;
import org.refit.spring.ceo.entity.ReceiptDetail;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateCardDetailDto {
    private Long receiptId;         // 영수증
    private String companyName;     // 상호명
    private Long totalPrice;        // 주문 합계
    private String receiptDateTime; // 결제 일시
    private String processState;    // 영수처리 여부
    private boolean corporate;      // 법인 카드 여부

    public static CorporateCardDetailDto of(CorporateCardDetail vo) {
        CorporateCardDetailDto CorporateCardDetail = CorporateCardDetailDto.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .receiptDateTime(vo.getReceiptDateTime())
                .processState(vo.getProcessState())
                .corporate(vo.isCorporate())
                .build();

        return CorporateCardDetail;
    }
}
