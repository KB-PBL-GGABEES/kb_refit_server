package org.refit.spring.hospital.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsuranceClaimRequestDto {
    private String receiptId;
    private String sickedDate;
    private String visitedReason;
}
