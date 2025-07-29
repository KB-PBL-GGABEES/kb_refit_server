package org.refit.spring.receipt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class ReceiptDetailDto {
    private Long userId;
    private Long receiptId;
    private Long companyId;
    private String companyName;
    private String address;
    private List<ReceiptContentDto> receiptContents;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    private String processState;
    private String cardNumber;
    private Integer isCorporate;
}
