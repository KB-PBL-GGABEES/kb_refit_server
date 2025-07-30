// OpenApiValidateRequestDto.java
package org.refit.spring.receiptProcess.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
public class OpenApiValidateRequestDto {

    private List<Business> businesses;

    @Getter
    @AllArgsConstructor
    public static class Business {
        private String b_no;       // 사업자번호
        private String start_dt;   // 개업일자 (yyyyMMdd)
        private String p_nm;       // 대표자명
        private String p_nm2;      // 외국인 아닌 경우 빈 문자열
        private String b_nm;       // 상호 (빈 문자열)
        private String corp_no;    // 법인번호 (빈 문자열)
        private String b_sector;   // 업태
        private String b_type;     // 종목
        private String b_adr;      // 주소
    }

    public static OpenApiValidateRequestDto from(CheckCompanyRequestDto dto) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = formatter.format(dto.getOpenedDate());

        Business business = new Business(
                dto.getCompanyId(),
                formattedDate,
                dto.getCeoName(),
                "", "", "", "", "", ""
        );

        return new OpenApiValidateRequestDto(Collections.singletonList(business));
    }
}