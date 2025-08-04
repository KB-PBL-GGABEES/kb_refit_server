package org.refit.spring.receipt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.refit.spring.receipt.dto.ReceiptContentDetailDto;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Receipt {
    @ApiModelProperty(value = "영수증 아이디", example = "1")
    private Long receiptId;
    @ApiModelProperty(value = "총 가격", example = "12300")
    private Long totalPrice;
    @ApiModelProperty(value = "공급가액", example = "11181")
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updatedAt;
    List<ReceiptContentDetailDto> contentList;
    private Long companyId;
    private Long userId;
    private Long cardId;
    private String processState;
    private String companyName;
}
