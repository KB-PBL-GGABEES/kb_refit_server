package org.refit.spring.receiptProcess.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * OpenAPI(국세청 사업자등록 진위확인) 요청 시 사용하는 DTO
 * - JSON 형식으로 외부에 전달되며, 'businesses' 필드를 포함함
 * - 하나 이상의 사업자 정보를 담을 수 있도록 List 형태로 구성됨
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenApiValidateRequestDto {

    // OpenAPI 요청 바디의 최상위 필드: 사업자 정보 리스트
    private List<Business> businesses;

    /**
     * 내부 정적 클래스 Business
     * - OpenAPI에 전달할 단일 사업자 정보 구조
     * - b_no: 사업자 등록번호 (문자열)
     * - start_dt: 개업일자 (yyyyMMdd 포맷)
     * - p_nm: 대표자 이름
     */
    @JsonPropertyOrder({"companyId", "ceoName", "openedDate", "isValid"})
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Business {
        private String b_no;     // 사업자번호
        private String start_dt; // 개업일자 (yyyyMMdd)
        private String p_nm;     // 대표자명
    }

    public static OpenApiValidateRequestDto from(CheckCompanyRequestDto dto) {
        return OpenApiValidateRequestDto.builder()
                .businesses(Collections.singletonList(
                        Business.builder()
                                .b_no(dto.getCompanyId())
                                .start_dt(new java.text.SimpleDateFormat("yyyyMMdd").format(dto.getOpenedDate()))
                                .p_nm(dto.getCeoName())
                                .build()
                ))
                .build();
    }
}