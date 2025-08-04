package org.refit.spring.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class MedicalReceiptListCursorDto {
    private List<MedicalReceiptListDto> hospitalList;
    private Long nextCursorId;

    public static MedicalReceiptListCursorDto from(List<MedicalReceiptListDto> list, Long nextCursorId) {
        return new MedicalReceiptListCursorDto(list, nextCursorId);
    }
}