package com.n26.model;

import java.time.Instant;

/**
 * @author Anupam
 */
public class Transaction {

    private String amount;
    private String timestamp;
    private Long longTimestamp;

    public Transaction() {

    }

    public Transaction(String amount, String timestamp) {
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Long getLongTimestamp() {
        Instant instant= Instant.parse(getTimestamp());
        return instant.toEpochMilli();
    }

    @Override
    public String toString() {
        return "amount=" + amount +
                ", timestamp=" + timestamp;
    }
}
