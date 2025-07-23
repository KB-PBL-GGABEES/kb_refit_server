package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InsuranceSubscribedResponseDto {
    private Long insuranceId;
    private String insuranceName;
    private Date joinedDate;
}
