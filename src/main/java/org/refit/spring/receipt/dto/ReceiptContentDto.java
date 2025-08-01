package org.refit.spring.receipt.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReceiptContentDto {
    @ApiModelProperty(value = "상품 아이디", example = "1")
    private Long merchandiseId;
    @ApiModelProperty(value = "상품명", example = "카페 아메리카노")
    private String merchandiseName;
    @ApiModelProperty(value = "상품 가격", example = "4500")
    private Long merchandisePrice;
    @ApiModelProperty(value = "구매 개수", example = "3")
    private Long amount;
}
