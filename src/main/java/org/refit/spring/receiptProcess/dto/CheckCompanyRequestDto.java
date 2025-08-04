    package org.refit.spring.receiptProcess.dto;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import lombok.*;

    import java.util.Date;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class CheckCompanyRequestDto {
        private String companyId;     // 문자열로 받는 게 OpenAPI 응답과 맞춤
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd", timezone = "Asia/Seoul")
        private Date openedDate;
        private String ceoName;
    }
