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
        private String companyId;
        private String ceoName;
        private String openedDate;
}