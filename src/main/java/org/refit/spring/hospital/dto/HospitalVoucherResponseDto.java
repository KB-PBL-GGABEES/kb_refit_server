package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HospitalVoucherResponseDto {
    private String hospitalVoucher;

    public HospitalVoucherResponseDto(String hospitalVoucher) {
        this.hospitalVoucher = hospitalVoucher;
    }
}