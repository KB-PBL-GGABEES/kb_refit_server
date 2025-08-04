package org.refit.spring.hospital.entity;

import lombok.Data;

import java.util.Date;

@Data
public class HospitalProcess {
    private Long hospitalProcessId;
    private String processState;
    private Date sickedDate;
    private String visitedReason;
    private Long receiptId;
    private Long insuranceId;
    private String hospitalVoucher;
}
