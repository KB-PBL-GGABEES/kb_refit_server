package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HospitalExpenseResponseDto {
    private Date createdAt;
    private String storeName;
    private String processState;
    private Long totalPrice;
}
