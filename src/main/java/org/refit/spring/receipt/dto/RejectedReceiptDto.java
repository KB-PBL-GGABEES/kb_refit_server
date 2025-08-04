package org.refit.spring.receipt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class RejectedReceiptDto {
    private Long receiptId;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date updatedAt;
    private Long companyId;
    private String companyName;
    private Long userId;
    private Long cardId;
    private String processState;
    private Long receiptProcessId;
    private List<ReceiptContentDetailDto> contentList;
}
