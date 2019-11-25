package com.dst.tpc.test;

import com.dst.tpc.Transaction;
import com.dst.tpc.TransactionFailException;

public class MockTransaction implements Transaction {

    protected String name;

    public MockTransaction(String name) {
        this.name = name;
    }

    @Override
    public void execute() throws TransactionFailException {
        System.out.println("[" + name + "] 事务被执行");
    }

    @Override
    public void rollback() {
        System.out.println("[" + name + "] 事务被回滚");
    }
}
