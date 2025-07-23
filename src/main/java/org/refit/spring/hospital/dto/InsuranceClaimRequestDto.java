package org.refit.spring.hospital.dto;


import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class InsuranceClaimRequestDto {
    private Long receiptId;
    private Date sickedDate;
    private String visitedReason;
}
