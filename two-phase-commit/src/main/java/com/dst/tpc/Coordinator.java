package com.dst.tpc;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface Coordinator {
    void addParticipant(Participant participant);
    void setParticipants(List<Participant> participants);
    List<Participant> getParticipants();
    void setTimeout(long timeout, TimeUnit timeoutUnit);
    void transactionCommitQuery();
    void acceptFeedback();
    void transactionCommitPerform();
    boolean isTimeout();
}
