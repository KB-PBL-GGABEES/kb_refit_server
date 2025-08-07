package org.refit.spring.hospital.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class MedicalReceiptDetailDto {
    private String hospitalName;
    private Long companyId;
    private String ceoName;
    private String address;
    private Long supplyPrice;
    private Long surtax;
    private Long totalPrice;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date createdAt;
    private String processState;
    private String rejectedReason;
}