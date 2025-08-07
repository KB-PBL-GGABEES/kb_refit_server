package org.refit.spring.ceo.enums;

public enum State {
    Whole, Process, UnProcess;

    public boolean Process() {
        return this == Process;
    }

    public boolean UnProcess() {
        return this == UnProcess;
    }
}
