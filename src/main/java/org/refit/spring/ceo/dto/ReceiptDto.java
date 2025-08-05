package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.Ceo;

import java.time.format.DateTimeFormatter;

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
    private String createdAt;
    @ApiModelProperty(value = "영수 처리 상태", example = "none")
    private String processState;
    @ApiModelProperty(value = "커서 ID", example = "3")
    private Long cursorId;

    public static ReceiptDto of(Ceo vo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ReceiptDto ceo = ReceiptDto.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .createdAt(
                        vo.getCreatedAt() != null
                                ? vo.getCreatedAt().format(formatter)
                                : null)
                .processState(vo.getProcessState())
                .cursorId(vo.getReceiptId())
                .build();

        return ceo;
    }
}
