package org.refit.spring.hospital.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Insurance {
    private Long insuranceId;
    private String insuranceName;
    private Date joinedDate;
    private Long userId;
}
