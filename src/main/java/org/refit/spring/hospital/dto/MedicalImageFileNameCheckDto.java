package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedicalImageFileNameCheckDto {
    private String medicalImageFileName;

    public MedicalImageFileNameCheckDto(String medicalImageFileName) {
        this.medicalImageFileName = medicalImageFileName;
    }
}