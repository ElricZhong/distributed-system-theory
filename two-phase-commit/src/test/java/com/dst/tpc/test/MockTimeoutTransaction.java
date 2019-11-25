package com.dst.tpc.test;

import com.dst.tpc.TransactionFailException;

public class MockTimeoutTransaction extends MockTransaction {

    private long timeout;

    public MockTimeoutTransaction(String transaction, long timeout) {
        super(transaction);
        this.timeout = timeout;
    }

    @Override
    public void execute() throws TransactionFailException {
        try {
            super.execute();
            Thread.sleep(timeout);
        } catch (InterruptedException ignored) { }
    }
}
