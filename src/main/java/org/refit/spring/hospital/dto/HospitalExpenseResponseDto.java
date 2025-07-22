package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class HospitalExpenseResponseDto {
    private Date createdAt;
    private String storeName;
    private boolean processState;
    private Long totalPrice;
}
