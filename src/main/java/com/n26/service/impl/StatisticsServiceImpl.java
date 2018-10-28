package com.n26.service.impl;

import com.n26.cache.TransactionCacheManager;
import com.n26.model.Statistics;
import com.n26.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
    private TransactionCacheManager transactionCacheManager;

    @Override
    public Statistics retrieveStatistics() {
       return transactionCacheManager.retrieveStatistics();
    }
}
