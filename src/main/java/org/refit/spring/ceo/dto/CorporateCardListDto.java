package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.CorporateCardList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateCardListDto {
    private Long receiptId;         // 영수증
    @ApiModelProperty(value = "가게명", example = "이디야커피")
    private String companyName;     // 상호명
    @ApiModelProperty(value = "주문합계", example = "4100")
    private Long totalPrice;        // 주문 합계
    @ApiModelProperty(value = "결제 일시", example = "2025-07-27 09:30:00")
    private String receiptDateTime; // 결제 일시
    @ApiModelProperty(value = "영수 처리 상태", example = "null")
    private String processState;    // 영수처리 여부
    @ApiModelProperty(value = "법인카드 여부", example = "true")
    private boolean corporate;      // 법인 카드 여부

    public static CorporateCardListDto of(CorporateCardList vo) {
        CorporateCardListDto CorporateCardList = CorporateCardListDto.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .receiptDateTime(vo.getReceiptDateTime())
                .processState(vo.getProcessState())
                .corporate(vo.isCorporate())
                .build();

        return CorporateCardList;
    }
}
