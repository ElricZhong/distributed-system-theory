package com.dst.tpc;

public class TransactionFailException extends Exception {
    public TransactionFailException(Throwable cause) {
        super(cause);
    }
}
