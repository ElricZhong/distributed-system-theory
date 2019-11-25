package com.dst.tpc;

public interface Participant {
    void executeTransaction(Coordinator coordinator);
    void commitTransaction(Coordinator coordinator);
    void rollbackTransaction(Coordinator coordinator);
    boolean isDone();
    boolean isCommitted();
    boolean isRollback();
}
