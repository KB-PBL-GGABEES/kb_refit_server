package org.refit.spring.receiptProcess.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

// 사업자 정보 확인 요청
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckCompanyResponseDto {
        private boolean isValid;
        private Long companyId;
        private String companyName;
        private String ceoName;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd", timezone = "Asia/Seoul")
        private Date openedDate;
}