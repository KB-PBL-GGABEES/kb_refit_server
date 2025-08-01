package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private Long companyId;
    private Long ceoId;
    private String companyName;
    private String ceoName;
    private String address;
    private Date openedDate;
    private Date createdAt;
    private Date updatedAt;
    private Long categoryId;
}
