package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.refit.spring.ceo.entity.ReceiptList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptListDto {
    private Long userid;            // 사장님
    private String name;            // 사장님 이름
    private String progressType;    // 경비 처리 항목
    private String progressDetail;  // 세부 내용
    private String voucher;         // 증빙 이미지 파일명
    private Long receiptId;         // 영수증

    public static ReceiptListDto of(ReceiptList vo) {
        ReceiptListDto receiptList = ReceiptListDto.builder()
                .userid(vo.getUserid())
                .name(vo.getName())
                .progressType(vo.getProgressType())
                .progressDetail(vo.getProgressDetail())
                .voucher(vo.getVoucher())
                .receiptId(vo.getReceiptId())
                .build();

        return receiptList;
    }
}