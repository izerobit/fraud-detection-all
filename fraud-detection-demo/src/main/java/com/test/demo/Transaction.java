package com.test.demo;

public class Transaction {
    private long transactionId;
    private long timestamp;
    private PaymentType paymentType;
    private long paymentAmount;
    private String appName;

    public Transaction(){}

    public Transaction(long transactionId, long timestamp, PaymentType paymentType, long paymentAmount, String appName) {
        this.transactionId = transactionId;
        this.timestamp = timestamp;
        this.paymentType = paymentType;
        this.paymentAmount = paymentAmount;
        this.appName = appName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public long getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(long paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", timestamp=" + timestamp +
                ", paymentType=" + paymentType +
                ", paymentAmount=" + paymentAmount +
                ", appName='" + appName + '\'' +
                '}';
    }
}
