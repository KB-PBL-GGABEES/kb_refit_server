package org.refit.spring.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class HospitalListDto {
    private List<HospitalExpenseResponseDto> hospitalList;
    private Long nextCursorId;

    public static HospitalListDto from(List<HospitalExpenseResponseDto> list, Long nextCursorId) {
        return new HospitalListDto(list, nextCursorId);
    }
}