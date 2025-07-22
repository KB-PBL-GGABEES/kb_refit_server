package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.domain.CeoVO;
import org.refit.spring.ceo.domain.ReceiptDetailVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDetailDTO {
    private Long userid;            // 사장님
    private String name;            // 사장님 이름
    private String progressType;    // 경비 처리 항목
    private String progressDetail;  // 세부 내용
    private Long receiptId;         // 영수증

    public static ReceiptDetailDTO of(ReceiptDetailVO vo) {
        ReceiptDetailDTO receiptDetail = ReceiptDetailDTO.builder()
                .userid(vo.getUserid())
                .name(vo.getName())
                .progressType(vo.getProgressType())
                .progressDetail(vo.getProgressDetail())
                .receiptId(vo.getReceiptId())
                .build();

        return receiptDetail;
    }
}