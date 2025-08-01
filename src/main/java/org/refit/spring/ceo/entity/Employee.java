package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    private Long employeeId;
    private Date startDate;
    private Date endDate;
    private Long userId;
    private Long companyId;
}
