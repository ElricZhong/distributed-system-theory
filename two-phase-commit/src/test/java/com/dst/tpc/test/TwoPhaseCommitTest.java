package com.dst.tpc.test;

import com.dst.tpc.Coordinator;
import com.dst.tpc.Participant;
import com.dst.tpc.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TwoPhaseCommitTest {

    private Coordinator coordinator;

    @Before
    public void setUp() throws Exception {
        coordinator = new MockCoordinator("coordinator");
        coordinator.setTimeout(3, TimeUnit.SECONDS);
    }

    @Test
	public void testAllParticipantsExecuteTransactionSuccessInPhase1() throws Exception {
        coordinator.setParticipants(createNormalMockParticipants(3));

		// 1.提交事务请求
		coordinator.transactionCommitQuery();

		for (Participant participant : coordinator.getParticipants()) {
			assertTrue(participant.isDone());
			assertFalse(participant.isCommitted());
		}
	}

	@Test
	public void testOneParticipantExecuteTransactionFailInPhase1() throws Exception {
		coordinator.addParticipant(createMockParticipant("participant 0", new MockTransaction("transaction 0")));
		coordinator.addParticipant(createMockParticipant("participant 1", new MockTransaction("transaction 1")));
		coordinator.addParticipant(createMockParticipant("participant 2", new MockFailTransaction("transaction 2")));

		// 1.提交事务请求
		coordinator.transactionCommitQuery();

        List<Participant> participants = coordinator.getParticipants();
		assertFalse(participants.get(2).isDone());

		for (Participant participant : participants) {
			assertFalse(participant.isCommitted());
			assertFalse(participant.isRollback());
		}
	}

	@Test
	public void testAllParticipantsCommitTransactionSuccessInPhase2() throws Exception {
        coordinator.setParticipants(createNormalMockParticipants(3));

		// 1.提交事务请求
		coordinator.transactionCommitQuery();
		// 2.执行事务提交
		coordinator.transactionCommitPerform();

		for (Participant participant : coordinator.getParticipants()) {
			assertTrue(participant.isCommitted());
		}
	}

	@Test
	public void testOneParticipantExecuteTransactionFailInPhase1ThenRollbackInPhase2() throws Exception {
        coordinator.addParticipant(createMockParticipant("participant 0", new MockTransaction("transaction 0")));
        coordinator.addParticipant(createMockParticipant("participant 1", new MockTransaction("transaction 1")));
        coordinator.addParticipant(createMockParticipant("participant 2", new MockFailTransaction("transaction 2")));

		// 1.提交事务请求
		coordinator.transactionCommitQuery();

        List<Participant> participants = coordinator.getParticipants();
		assertFalse(participants.get(2).isDone());

		// 2.执行事务提交
		coordinator.transactionCommitPerform();
		for (Participant participant : coordinator.getParticipants()) {
			assertTrue(participant.isRollback());
			assertFalse(participant.isCommitted());
		}
	}

	@Test
	public void testOneParticipantExecuteTransactionTimeoutInPhase1ThenRollbackInPhase2() throws Exception {
        coordinator.addParticipant(createMockParticipant("participant 0", new MockTransaction("transaction 0")));
        coordinator.addParticipant(createMockParticipant("participant 1", new MockTransaction("transaction 1")));
        coordinator.addParticipant(createMockParticipant("participant 2", new MockTimeoutTransaction("transaction 2", 5000)));

		// 1.提交事务请求
		coordinator.transactionCommitQuery();
		assertTrue(coordinator.isTimeout());

		// 2.执行事务提交
		coordinator.transactionCommitPerform();
		for (Participant participant : coordinator.getParticipants()) {
			assertTrue(participant.isRollback());
			assertFalse(participant.isCommitted());
		}
	}

	public List<Participant> createNormalMockParticipants(int n) {
        ArrayList<Participant> participants = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            participants.add(createMockParticipant("participant " + i,
                    new MockTransaction("transaction " + i)));
        }
        return participants;
    }

    public Participant createMockParticipant(String participantName, Transaction transaction) {
        return new MockParticipant(participantName, transaction);
    }
}
