package org.refit.spring.ceo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.Ceo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "경비 처리 영수증 DTO")
public class ReceiptDto {
    @ApiModelProperty(value = "영수증 ID", example = "1")
    private Long receiptId;
    @ApiModelProperty(value = "가게명", example = "스타벅스")
    private String companyName;
    @ApiModelProperty(value = "주문합계", example = "5900")
    private Long totalPrice;
    @ApiModelProperty(value = "결제 일시", example = "2025-05-13 14:10:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @ApiModelProperty(value = "영수 처리 상태", example = "none")
    private String processState;

    public static ReceiptDto of(Ceo vo) {
        ReceiptDto ceo = ReceiptDto.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .createdAt(vo.getCreatedAt())
                .processState(vo.getProcessState())
                .build();

        return ceo;
    }
}
