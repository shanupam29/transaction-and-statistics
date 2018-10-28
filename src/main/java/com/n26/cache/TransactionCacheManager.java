package com.n26.cache;

import com.n26.model.Statistics;
import com.n26.model.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCacheManager {
    void addTransaction(Transaction transaction);
    void deleteAllTransactions();
    Statistics retrieveStatistics();
}
