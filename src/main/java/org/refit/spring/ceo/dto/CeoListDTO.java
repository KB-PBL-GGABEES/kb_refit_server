package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.domain.CeoVO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CeoListDTO {
    private Long receiptId;
    private String companyName; // 상호명
    private Long totalPrice;    // 결제금액
    private String receiptDate; // 결제일
    private String receiptTime; // 결제시간

    // 영수처리에 대한 상태 확인이 없음
    // 현재는 영수증 리스트 그 자체만 가져오고 있음
    private String processState; // 영수처리 여부

//    private String storeImage;  // 상호이미지

    public static CeoListDTO of(CeoVO vo) {
        CeoListDTO ceo = CeoListDTO.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .receiptDate(vo.getReceiptDate())
                .receiptTime(vo.getReceiptTime())
//                .storeImage(vo.getStoreImage())
                .processState(vo.getProcessState())
                .build();

        return ceo;
    }
}
