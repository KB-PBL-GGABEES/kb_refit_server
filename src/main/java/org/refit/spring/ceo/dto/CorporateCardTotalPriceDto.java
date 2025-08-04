package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "법인카드 한달 총액 DTO")
public class CorporateCardTotalPriceDto {
    @ApiModelProperty(value = "이번 달 (월)", example = "8")
    private int month;
    @ApiModelProperty(value = "이번 달 총 금액", example = "39000")
    private Long thisMonth;
    @ApiModelProperty(value = "지난 달 총 금액", example = "41000")
    private Long lastMonth;
}
