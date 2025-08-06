package org.refit.spring.receipt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.refit.spring.receipt.entity.Receipt;

import java.util.Date;

@Data
@AllArgsConstructor
public class HospitalReceiptResponseDto {
    private Long userId;
    private Long receiptId;
    private Long totalPrice;
    private Long supplyPrice;
    private Long surtax;
    private Long carbonPoint;
    private Long reward;
    private String processState;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    private String companyName;

    public static HospitalReceiptResponseDto from(Receipt receipt, Long userId, Long carbon, Long price, String processState) {
        return new HospitalReceiptResponseDto(
                userId,
                receipt.getReceiptId(),
                receipt.getTotalPrice(),
                receipt.getSupplyPrice(),
                receipt.getSurtax(),
                carbon,
                price,
                processState != null ? processState : "none",
                receipt.getCreatedAt(),
                receipt.getCompanyName()
        );
    }
}
