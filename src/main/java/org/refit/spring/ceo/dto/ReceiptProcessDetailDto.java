package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.receipt.dto.ReceiptDetailDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "경비 처리 상세 정보 DTO")
public class ReceiptProcessDetailDto {
    @ApiModelProperty(value = "영수증 상세 정보")
    private ReceiptDetailDto receiptDetail;

    @ApiModelProperty(value = "영수처리 신청자 상세 정보")
    private ReceiptProcessApplicantDto receiptInfo;
}
