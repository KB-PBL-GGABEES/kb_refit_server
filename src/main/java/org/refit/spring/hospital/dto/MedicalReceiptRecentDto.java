package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicalReceiptRecentDto {
    private Long recentTotalPrice;
    private Long insuranceBillable;
}
