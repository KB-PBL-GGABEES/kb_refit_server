package org.refit.spring.ceo.enums;

// 법인카드 내역에 대해
public enum RejectState {
    Whole, UnRejected, Rejected;

    public boolean UnRejected() {
        return this == UnRejected;
    }

    public boolean Rejected() {
        return this == Rejected;
    }
}
