package org.refit.spring.receipt.enums;

public enum ReceiptSort {
    최신순, 과거순;

    public String toOrderBy() {
        return this == 과거순 ? "ASC" : "DESC";
    }
}
