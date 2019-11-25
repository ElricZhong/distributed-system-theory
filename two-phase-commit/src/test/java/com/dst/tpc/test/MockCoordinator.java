package com.dst.tpc.test;

import com.dst.tpc.Coordinator;
import com.dst.tpc.Participant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockCoordinator implements Coordinator {

    private String name;
    private List<Participant> participants;
    private volatile CountDownLatch countDownLatch;
    private long timeout;
    private TimeUnit timeoutUnit;
    private volatile boolean isTimeout;

    public MockCoordinator(String name) {
        this.name = name;
        participants = new ArrayList<>();
    }

    @Override
    public void addParticipant(Participant participant) {
        participants.add(participant);
    }

    @Override
    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public List<Participant> getParticipants() {
        return participants;
    }

    @Override
    public void setTimeout(long timeout, TimeUnit timeoutUnit) {
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    @Override
    public void transactionCommitQuery() {
        System.out.println("[" + name + "] 开始请求提交事务");
        countDownLatch = new CountDownLatch(participants.size());
        for (Participant participant : participants) {
            new Thread(() -> participant.executeTransaction(this)).start();
        }
        try {
            isTimeout = !countDownLatch.await(timeout, timeoutUnit);
        } catch (InterruptedException ignored) { }
        System.out.println("[" + name + "] 结束请求提交事务");
    }

    @Override
    public void acceptFeedback() {
        countDownLatch.countDown();
    }

    @Override
    public void transactionCommitPerform() {
        System.out.println("[" + name + "] 开始执行事务提交开始");
        countDownLatch = new CountDownLatch(participants.size());
        if (isTimeout) {
            System.out.println("[" + name + "] 等待超时");
        }

        final boolean canCommit = (!isTimeout) &&
                (participants.stream().allMatch(participant -> participant.isDone()));
        for (Participant participant : participants) {
            new Thread(() -> commitTransactionPerParticipant(participant, canCommit)).start();
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException ignored) { }
        System.out.println("[" + name + "] 结束执行事务提交");
    }

    private void commitTransactionPerParticipant(Participant participant, boolean canCommit) {
        if (canCommit) {
            participant.commitTransaction(this);
        } else {
            participant.rollbackTransaction(this);
        }
    }

    @Override
    public boolean isTimeout() {
        return isTimeout;
    }
}
