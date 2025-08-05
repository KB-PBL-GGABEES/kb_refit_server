package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
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
@ApiModel(description = "법인카드 영수증 DTO")
public class CorporateCardListDto {
    private Long receiptId;
    @ApiModelProperty(value = "가게명", example = "이디야커피")
    private String companyName;
    @ApiModelProperty(value = "주문합계", example = "4100")
    private Long totalPrice;
    @ApiModelProperty(value = "결제 일시", example = "2025-07-27 09:30:00")
    private String createdAt;
    @ApiModelProperty(value = "영수 처리 상태", example = "null")
    private String processState;
    @ApiModelProperty(value = "법인카드 여부", example = "true")
    private boolean corporate;
    @ApiModelProperty(value = "커서 ID", example = "3")
    private Long cursorId;

    public static CorporateCardListDto of(CorporateCardList vo) {
        CorporateCardListDto CorporateCardList = CorporateCardListDto.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .createdAt(vo.getCreatedAt())
                .processState(vo.getProcessState())
                .corporate(vo.isCorporate())
                .cursorId(vo.getReceiptId())
                .build();

        return CorporateCardList;
    }
}
