package org.refit.spring.reward.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.refit.spring.receipt.enums.ReceiptSort;
import org.refit.spring.reward.enums.RewardType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class RewardListRequestDto {
    private Long cursorId;
    private Integer period;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDate;
    private RewardType type;
    private ReceiptSort sort;
    private Long size;
}
