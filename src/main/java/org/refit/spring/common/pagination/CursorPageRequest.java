package org.refit.spring.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageRequest {
    private String cursor;
    private Integer size;
    public static final int DEFAULT_PAGE_SIZE = 20;

    public int getSize() {
        return (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : size;
    }
}
