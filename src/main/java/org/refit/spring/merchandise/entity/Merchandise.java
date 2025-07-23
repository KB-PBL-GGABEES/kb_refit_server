package org.refit.spring.merchandise.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Merchandise {
    private Long merchandiseId;
    private String merchandiseName;
    private Long merchandisePrice;
    private Long companyId;
}
