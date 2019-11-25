package com.dst.tpc.test;

import com.dst.tpc.Coordinator;
import com.dst.tpc.Participant;
import com.dst.tpc.Transaction;
import com.dst.tpc.TransactionFailException;

public class MockParticipant implements Participant {

    private String name;
    private Transaction transaction;
    private volatile boolean isDone;
    private volatile boolean isCommitted;
    private volatile boolean isRollback;

    public MockParticipant(String name, Transaction transaction) {
        this.name = name;
        this.transaction = transaction;
    }

    @Override
    public void executeTransaction(Coordinator coordinator) {
        startTransaction();
        try {
            transaction.execute();
            isDone = true;
        } catch (TransactionFailException e) {
            e.printStackTrace();
            isDone = false;
        } finally {
            coordinator.acceptFeedback();
        }
    }

    @Override
    public void commitTransaction(Coordinator coordinator) {
        commitTransaction();
        isCommitted = true;
        coordinator.acceptFeedback();
    }

    @Override
    public void rollbackTransaction(Coordinator coordinator) {
        rollbackTransaction();
        isCommitted = false;
        isRollback = true;
        coordinator.acceptFeedback();
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public boolean isCommitted() {
        return isCommitted;
    }

    @Override
    public boolean isRollback() {
        return isRollback;
    }

    private void startTransaction() {
        System.out.println("[" + name + "] 开始执行事务");
    }

    private void commitTransaction() {
        System.out.println("[" + name + "] 提交事务成功");
    }

    private void rollbackTransaction() {
        System.out.println("[" + name + "] 开始回滚事务");
        transaction.rollback();
        isRollback = true;
        System.out.println("[" + name + "] 回滚事务成功");
    }
}
