package org.refit.spring.ceo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
}
