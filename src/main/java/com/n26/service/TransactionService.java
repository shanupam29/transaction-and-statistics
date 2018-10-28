package com.n26.service;

import com.n26.model.Transaction;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    public static final int TIME_OUT_DURATION = 60000;
    void addTransaction(Transaction transaction);
    void clearTransactions();
}
