package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HospitalImageFileNameCheckDto {
    private String hospitalVoucher;

    public HospitalImageFileNameCheckDto(String hospitalVoucher) {
        this.hospitalVoucher = hospitalVoucher;
    }
}