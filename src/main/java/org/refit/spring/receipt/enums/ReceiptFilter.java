package org.refit.spring.receipt.enums;

public enum ReceiptFilter {
    ALL,
    PROCESSED,
    UNPROCESSED;

    public boolean isProcessed() {
        return this == PROCESSED;
    }
}
