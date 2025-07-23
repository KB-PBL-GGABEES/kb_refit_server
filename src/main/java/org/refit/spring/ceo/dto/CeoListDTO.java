package org.refit.spring.ceo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.domain.CeoVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CeoListDTO {
    private Long receiptId;
    private String companyName; // 상호명
    private Long totalPrice;    // 결제금액
    private String receiptDateTime; // 결제 일시
    private String processState; // 영수처리 여부

//    private String storeImage;  // 상호이미지

    public static CeoListDTO of(CeoVO vo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        CeoListDTO ceo = CeoListDTO.builder()
                .receiptId(vo.getReceiptId())
                .companyName(vo.getCompanyName())
                .totalPrice(vo.getTotalPrice())
                .receiptDateTime(
                        vo.getReceiptDateTime() != null
                                ? vo.getReceiptDateTime().format(formatter)
                                : null)
//                .storeImage(vo.getStoreImage())
                .processState(vo.getProcessState())
                .build();

        return ceo;
    }
}
