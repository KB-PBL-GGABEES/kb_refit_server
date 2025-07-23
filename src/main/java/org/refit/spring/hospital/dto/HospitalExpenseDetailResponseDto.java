package org.refit.spring.hospital.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HospitalExpenseDetailResponseDto {
    private String hospitalName;
    private Long companyId;
    private String bossName;
    private String address;
    private Long supplyPrice;
    private Long surtax;
    private String transactionType;
    private Date createdAt;
}
