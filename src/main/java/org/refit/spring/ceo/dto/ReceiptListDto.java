package org.refit.spring.ceo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.ReceiptList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "경비 청구 항목 상세 DTO")
public class ReceiptListDto {
    @ApiModelProperty(value = "사장님 ID", example = "1")
    private Long userid;
    @ApiModelProperty(value = "사장님 이름", example = "조승연")
    private String name;
    @ApiModelProperty(value = "경비 처리 항목", example = "업무 추진")
    private String documentType;
    @ApiModelProperty(value = "세부 내용", example = "업무 추진 간 타사 협력을 위한 카페 방문")
    private String documentDetail;
    @ApiModelProperty(value = "증빙 이미지 파일명", example = "샘플 이미지 파일명")
    private String imageFileName;
    @ApiModelProperty(value = "영수증 ID", example = "1")
    private Long receiptId;
    @ApiModelProperty(value = "영수 처리 상태", example = "none")
    private String processState;

    public static ReceiptListDto of(ReceiptList vo) {
        ReceiptListDto receiptList = ReceiptListDto.builder()
                .userid(vo.getUserid())
                .name(vo.getName())
                .documentType(vo.getDocumentType())
                .documentDetail(vo.getDocumentDetail())
                .imageFileName(vo.getImageFileName())
                .receiptId(vo.getReceiptId())
                .processState(vo.getProcessState())
                .build();

        return receiptList;
    }
}