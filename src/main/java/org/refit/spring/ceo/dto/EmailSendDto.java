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
@ApiModel(description = "경비 처리 완료 항목 이메일 보내기 DTO")
public class EmailSendDto {
    @ApiModelProperty(value = "이메일 주소", example = "test@naver.com")
    private String email;
//    @ApiModelProperty(value = "총 전송한 항목 수", example = "4")
//    private int numberSend;
}
