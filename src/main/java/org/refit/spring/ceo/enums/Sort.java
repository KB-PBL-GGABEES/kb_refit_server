package org.refit.spring.ceo.enums;

public enum Sort {
    NEWEST, OLDEST;

    public String toOrderBy() {
        return this == OLDEST ? "ASC" : "DESC";
    }
}
