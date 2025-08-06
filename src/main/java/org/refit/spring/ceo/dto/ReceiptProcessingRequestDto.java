package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "영수 처리 상태 변경 요청 DTO")
public class ReceiptProcessingRequestDto {

    @ApiModelProperty(value = "영수증 ID", required = true, example = "1")
    private Long receiptId;

    @ApiModelProperty(value = "처리 상태", required = true, example = "accepted")
    private String progressState;

    @ApiModelProperty(value = "반려 사유", example = "영수증 사진 누락")
    private String rejectedReason;
}
