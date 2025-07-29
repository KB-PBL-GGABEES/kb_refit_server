package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.ReceiptDetail;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailDto {
    private Long userid;            // 사장님
    private String name;            // 사장님 이름
    private String progressType;    // 경비 처리 항목
    private String progressDetail;  // 세부 내용
    private Long receiptId;         // 영수증

    public static ReceiptDetailDto of(ReceiptDetail vo) {
        ReceiptDetailDto receiptDetail = ReceiptDetailDto.builder()
                .userid(vo.getUserid())
                .name(vo.getName())
                .progressType(vo.getProgressType())
                .progressDetail(vo.getProgressDetail())
                .receiptId(vo.getReceiptId())
                .build();

        return receiptDetail;
    }
}