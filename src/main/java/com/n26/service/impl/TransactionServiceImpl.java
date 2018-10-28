package com.n26.service.impl;

import com.n26.cache.TransactionCacheManager;
import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionCacheManager transactionCacheManager;

    @Override
    public void addTransaction(Transaction transaction) {
        transactionCacheManager.addTransaction(transaction);
    }

    @Override
    public void clearTransactions() {
        transactionCacheManager.deleteAllTransactions();
    }
}
