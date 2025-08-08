package org.refit.spring.ceo.enums;

public enum RefundState {
    WHOLE, UNREFUND, FETUND;

    public boolean UnRefund() {
        return this == UNREFUND;
    }

    public boolean Refund() { return this == FETUND; }
}
