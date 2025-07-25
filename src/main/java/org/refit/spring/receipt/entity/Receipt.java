package org.refit.spring.receipt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.refit.spring.receipt.dto.ReceiptContentDto;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Receipt {
    private Long receiptId;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updatedAt;
    List<ReceiptContentDto> contentList;
    private Long companyId;
    private Long userId;
}
