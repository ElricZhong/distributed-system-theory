package com.dst.tpc;

public interface Transaction {
    void execute() throws TransactionFailException;
    void rollback();
}
