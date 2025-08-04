package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "경비 처리 영수증 상세 조회 DTO")
public class PendingDetailDto {
    @ApiModelProperty(value = "유저 ID", example = "1")
    private Long userId;
    @ApiModelProperty(value = "대기 항목 개수", example = "3")
    private int countPendingReceipts;
    @ApiModelProperty(value = "이번 달 완료 항목 개수", example = "4")
    private int countCompletedReceiptsThisMonth;
    @ApiModelProperty(value = "경비 처리 대기 내역 목록")
    private List<ReceiptDto> pendingReceipts;
}
