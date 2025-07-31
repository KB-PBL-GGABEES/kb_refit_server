package org.refit.spring.receipt.enums;

public enum ReceiptFilter {
    전체, 영수_처리, 영수_미처리;

    public boolean isProcessed() {
        return this == 영수_처리;
    }

    public boolean isUnprocessed() {
        return this == 영수_미처리;
    }
}
