package org.refit.spring.hospital.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.refit.spring.hospital.enums.HospitalFilter;
import org.refit.spring.hospital.enums.HospitalSort;
import org.refit.spring.hospital.enums.HospitalType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
public class MedicalListRequestDto {
    private Long cursorId;
    private Integer period;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private HospitalType type;
    private HospitalFilter filter;
    private HospitalSort sort;
}
