package org.refit.spring.receiptProcess.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
@AllArgsConstructor
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
    @Getter
    @AllArgsConstructor
    public static class Business {
        private String b_no;     // 사업자번호
        private String start_dt; // 개업일자 (yyyyMMdd)
        private String p_nm;     // 대표자명
    }

    // CheckCompanyRequestDto → OpenApiValidateRequestDto 변환 메서드
    // - 사용자가 입력한 사업자 정보를 OpenAPI 포맷에 맞게 감싸는 정적 팩토리 메서드
    public static OpenApiValidateRequestDto from(CheckCompanyRequestDto dto) {
        // Date → yyyyMMdd 문자열 포맷으로 변환
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = formatter.format(dto.getOpenedDate());

        // 단일 사업자 정보 객체 생성
        Business business = new Business(
                dto.getCompanyId(),  // b_no: 사업자 번호
                formattedDate,       // start_dt: 개업일자
                dto.getCeoName()     // p_nm: 대표자 이름
        );

        // 단일 Business 객체를 생성한 뒤,
        // 이를 단일 원소로 갖는 리스트에 담아 OpenAPI 요청 객체로 감쌈
        return new OpenApiValidateRequestDto(Collections.singletonList(business));
    }
}