package org.refit.spring.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CursorPageResponse<T> {
    private List<T> data;
    private Map<String, Object> cursor;

    public static <T> CursorPageResponse<T> of(List<T> dataList, int size, String cursorKey, Object nextCursorValue) {
        Map<String, Object> cursorMap = dataList.isEmpty()
                ? Map.of("message", "데이터가 없습니다.")
                : (dataList.size() < size ? Map.of("message", "마지막 페이지 입니다.") : Map.of(cursorKey, nextCursorValue));

        return new CursorPageResponse<>(dataList, cursorMap);
    }
}
