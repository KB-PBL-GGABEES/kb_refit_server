package org.refit.spring.ceo.enums;

public enum State {
    WHOLE, PROCESS, UNPROCESS, ACCEPTED, REJECTED, DEPOSIT, INPROGRESS;

    public boolean Process() { return this == PROCESS; }

    public boolean UnProcess() { return this == UNPROCESS; }

    public boolean Accepted() { return this == ACCEPTED; }

    public boolean Rejected() { return this == REJECTED; }

    public boolean Deposit() { return this == DEPOSIT; }

    public boolean InProgress() { return this == INPROGRESS; }
}
