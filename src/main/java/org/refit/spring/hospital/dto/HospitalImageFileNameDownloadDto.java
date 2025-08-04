package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HospitalImageFileNameDownloadDto {
    private Long receiptId;
    private String hospitalVoucher;
}