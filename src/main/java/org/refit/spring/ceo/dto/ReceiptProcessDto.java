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
@ApiModel(description = "영수 처리 결과 DTO")
public class ReceiptProcessDto {
    @ApiModelProperty(value = "결과 메시지", example = "영수 처리 완료")
    private String message;
    @ApiModelProperty(value = "처리 상태", example = "accepted")
    private String processStatus;
}
