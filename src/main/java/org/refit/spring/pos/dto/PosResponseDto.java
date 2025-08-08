package org.refit.spring.pos.dto;

import lombok.*;
import org.refit.spring.merchandise.entity.Merchandise;
import org.refit.spring.pos.entity.Company;

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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class GetCompanyDto {
        private Long companyId;
        private String companyName;
        private String ceoName;
        private String address;

        public static GetCompanyDto from(Company company) {
            return GetCompanyDto.builder()
                    .companyId(company.getCompanyId())
                    .companyName(company.getCompanyName())
                    .ceoName(company.getCeoName())
                    .address(company.getAddress())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class GetCompanyListDto {
        private List<GetCompanyDto> list;

        public static GetCompanyListDto from(List<GetCompanyDto> companyList) {
            return GetCompanyListDto.builder()
                    .list(companyList)
                    .build();
        }
    }
}
