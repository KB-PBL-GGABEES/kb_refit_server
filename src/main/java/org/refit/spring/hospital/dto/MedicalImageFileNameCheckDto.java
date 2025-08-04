package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedicalImageFileNameCheckDto {
    private String hospitalVoucher;

    public MedicalImageFileNameCheckDto(String hospitalVoucher) {
        this.hospitalVoucher = hospitalVoucher;
    }
}