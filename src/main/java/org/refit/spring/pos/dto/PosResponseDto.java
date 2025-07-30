package org.refit.spring.pos.dto;

import lombok.*;
import org.refit.spring.merchandise.entity.Merchandise;

import java.util.List;

public class PosResponseDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class GetMerchandiseDto {
        private Long merchandiseId;
        private String merchandiseName;
        private Long merchandisePrice;

        public static GetMerchandiseDto from(Merchandise merchandise) {
            return GetMerchandiseDto.builder()
                    .merchandiseId(merchandise.getMerchandiseId())
                    .merchandiseName(merchandise.getMerchandiseName())
                    .merchandisePrice(merchandise.getMerchandisePrice())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class GetMerchandiseListDto {
        private Long companyId;
        private List<GetMerchandiseDto> merchandiseList;

        public static GetMerchandiseListDto from(List<GetMerchandiseDto> merchandiseList, Long companyId) {
            return GetMerchandiseListDto.builder()
                    .companyId(companyId)
                    .merchandiseList(merchandiseList)
                    .build();
        }
    }
}
