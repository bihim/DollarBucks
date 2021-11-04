package com.tanvirhossen.dollarbucks.model;

public class TransactionModel {
    private String transactionMethod;
    private boolean isPending;

    public TransactionModel(String transactionMethod, boolean isPending) {
        this.transactionMethod = transactionMethod;
        this.isPending = isPending;
    }

    public String getTransactionMethod() {
        return transactionMethod;
    }

    public void setTransactionMethod(String transactionMethod) {
        this.transactionMethod = transactionMethod;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
