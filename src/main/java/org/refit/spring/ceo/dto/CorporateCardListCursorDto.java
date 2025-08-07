package org.refit.spring.ceo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CorporateCardListCursorDto {
    private List<CorporateCardDto> corporateCardList; // 변경
    private Long cursorId;

    public static CorporateCardListCursorDto from(List<CorporateCardDto> list, Long nextCursorId) {
        return new CorporateCardListCursorDto(list, nextCursorId);
    }
}
