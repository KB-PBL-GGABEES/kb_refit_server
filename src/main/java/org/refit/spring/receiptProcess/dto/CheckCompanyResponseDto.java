package org.refit.spring.receiptProcess.dto;

import lombok.*;

// 사업자 정보 확인 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckCompanyResponseDto {
        private boolean isValid;
        // 아래는 valid == 01일 때만 포함됨
        private Long companyId;
        private String companyName;
        private String address;
}
