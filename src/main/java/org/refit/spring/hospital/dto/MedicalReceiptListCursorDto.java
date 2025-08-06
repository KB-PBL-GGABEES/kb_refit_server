package org.refit.spring.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class MedicalReceiptListCursorDto {
    private List<MedicalReceiptDto> hospitalList;
    private Long nextCursorId;

    public static MedicalReceiptListCursorDto from(List<MedicalReceiptDto> list, Long nextCursorId) {
        return new MedicalReceiptListCursorDto(list, nextCursorId);
    }
}