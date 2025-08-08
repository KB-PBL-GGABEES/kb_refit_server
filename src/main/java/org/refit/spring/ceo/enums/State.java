package org.refit.spring.ceo.enums;

public enum State {
    Whole, Process, UnProcess, Accepted, Rejected, Deposit, InProgress;

    public boolean Process() {
        return this == Process;
    }

    public boolean UnProcess() { return this == UnProcess; }

    public boolean Rejected() { return this == Rejected; }

    public boolean Accepted() { return this == Accepted; }

    public boolean Deposit() { return this == Deposit; }

    public boolean InProgress() { return this == InProgress; }
}
