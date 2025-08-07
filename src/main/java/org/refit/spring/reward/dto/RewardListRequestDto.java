package org.refit.spring.reward.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.enums.RewardType;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RewardListRequestDto {
    private Long cursorId;
    private Integer period;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private RewardType type;
    private ReceiptSort sort;
    private Long size;
}
