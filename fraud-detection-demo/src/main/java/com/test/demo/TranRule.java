package com.test.demo;

public class TranRule {
    private int id;
    private Transaction transaction;
    private String key;
    private long timestamp;

    public TranRule(){}

    public TranRule(int id, String key, Transaction transaction, long timestamp){
        this.id = id;
        this.transaction = transaction;
        this.key = key;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
