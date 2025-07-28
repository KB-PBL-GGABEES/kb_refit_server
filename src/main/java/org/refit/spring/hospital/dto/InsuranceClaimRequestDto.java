package org.refit.spring.hospital.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class InsuranceClaimRequestDto {
    private String processState;
    private Long receiptId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date sickedDate;
    private String visitedReason;
    private Long insuranceId;
}
