package org.refit.spring.ceo.enums;

public enum Sort {
    Newest, Oldest;

    public String toOrderBy() {
        return this == Oldest ? "ASC" : "DESC";
    }
}
