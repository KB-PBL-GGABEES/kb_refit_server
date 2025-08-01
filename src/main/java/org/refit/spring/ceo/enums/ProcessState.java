package org.refit.spring.ceo.enums;

// 경비처리 완료된 항목에 대해
public enum ProcessState {
    Whole, Accepted, Rejected;

    public boolean Accepted() {
        return this == Accepted;
    }

    public boolean Rejected() {
        return this == Rejected;
    }
}
