package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InsuranceSubscribedResponseDto {
        private Long insuranceId;
        private String insuranceName;
        private String joinedDate;
    }
