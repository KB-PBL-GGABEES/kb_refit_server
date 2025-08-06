package org.refit.spring.hospital.enums;

import lombok.Getter;

@Getter
public enum HospitalFilter {
    ALL,
    PROCESSED,
    UNPROCESSED;

    public boolean isProcessed() {
        return this == PROCESSED;
    }
}

