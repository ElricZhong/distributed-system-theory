package com.dst.tpc.test;

import com.dst.tpc.TransactionFailException;

public class MockFailTransaction extends MockTransaction {

    public MockFailTransaction(String transaction) {
        super(transaction);
    }

    @Override
    public void execute() throws TransactionFailException {
        try {
            super.execute();
            throw new Exception("[" + name + "] 事务执行失败，抛出异常");
        } catch (Exception e) {
            throw new TransactionFailException(e);
        }
    }
}
