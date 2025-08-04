package org.refit.spring.hospital.enums;

import lombok.Getter;

@Getter
public enum HospitalFilter {
    ALL(null),            // 모든 상태
    ACCEPTED("accepted"), // 처리 완료
    REJECTED("rejected"), // 반려
    IN_PROGRESS("inProgress"), // 진행 중
    NONE("none");         // 미처리 or null

    private final String dbValue;

    HospitalFilter(String dbValue) {
        this.dbValue = dbValue;
    }

    public boolean isAll() {
        return this == ALL;
    }
}