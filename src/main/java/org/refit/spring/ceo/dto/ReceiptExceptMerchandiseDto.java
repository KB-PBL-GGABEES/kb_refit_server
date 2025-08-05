package org.refit.spring.ceo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptExceptMerchandiseDto {
    private Long userId;
    private Long receiptId;
    private Long companyId;
    private String companyName;
    private String ceoName;
    private String address;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    @ApiModelProperty(value = "영수 처리 여부", example = "accepted")
    private String processState;
    private String cardNumber;
    @ApiModelProperty(value = "법인 카드 여부", example = "1")
    private Integer isCorporate;
    private String rejectedReason;
}
