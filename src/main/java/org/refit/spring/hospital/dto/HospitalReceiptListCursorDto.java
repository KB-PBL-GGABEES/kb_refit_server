package org.refit.spring.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class HospitalReceiptListCursorDto {
    private List<HospitalReceiptListDto> hospitalList;
    private Long nextCursorId;

    public static HospitalReceiptListCursorDto from(List<HospitalReceiptListDto> list, Long nextCursorId) {
        return new HospitalReceiptListCursorDto(list, nextCursorId);
    }
}