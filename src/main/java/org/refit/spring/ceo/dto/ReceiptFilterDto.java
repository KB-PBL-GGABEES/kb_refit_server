package org.refit.spring.ceo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.refit.spring.ceo.enums.RefundState;
import org.refit.spring.ceo.enums.Sort;
import org.refit.spring.ceo.enums.State;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptFilterDto {
    private Long cursorId;
    private Integer period;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private State state;
    private RefundState refundState;    // 환불 관련
    private Sort sort;
    private Long size;
}
