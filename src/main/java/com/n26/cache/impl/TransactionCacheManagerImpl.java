package com.n26.cache.impl;

import com.n26.cache.TransactionCacheManager;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TransactionCacheManagerImpl implements TransactionCacheManager {

    private static final int SIXTY_SECONDS_IN_MILLIS = 60000;
    private static final int MILLIS_TO_PACK_SIZE = 100000000;
    private static final String ZERO = "0";

    private static ConcurrentHashMap<Long, Transaction> transactionsData;

    /**
     * Method to facilitate adding transactions into the
     * cache.
     *
     * @param transaction
     */
    @Override
    public void addTransaction(final Transaction transaction) {
        validateTransactionsData();
        System.out.println("Adding Transaction Data into cache :"+transaction.getLongTimestamp());
        transactionsData.putIfAbsent(packTransactionTimeStamp(transaction.getLongTimestamp()),transaction);
    }

    /**
     * Method to clear all the transactions from the cache and
     * reset the cache object.
     */
    @Override
    public void deleteAllTransactions() {
        if (null != transactionsData) {
            transactionsData.clear();
            transactionsData = new ConcurrentHashMap<>();
        }
    }

    /**
     * Method used to retrieve statistics of the transactions made
     * in the last 60 seconds.
     *
     * @return Statistics
     */
    @Override
    public synchronized Statistics retrieveStatistics() {
        long currentTime = Instant.now().toEpochMilli();
        if (null != transactionsData) {
            List<Double> transactionAmountsLstWithinSixtySecs = transactionsData.entrySet()
                    .stream()
                    .filter(transaction -> transaction.getValue().getLongTimestamp() > currentTime - SIXTY_SECONDS_IN_MILLIS)
                    .map(transaction -> Double.valueOf(transaction.getValue().getAmount())).collect(Collectors.toList());

            DoubleSummaryStatistics stats = transactionAmountsLstWithinSixtySecs.stream().
                    collect(Collectors.summarizingDouble(Double::doubleValue));
            return new Statistics(wrap(stats.getSum()), wrap(stats.getAverage()),
                    wrap(stats.getMax()), wrap(stats.getMin()), stats.getCount());
        }
        return new Statistics(ZERO, ZERO, ZERO, ZERO, 0L);
    }

    private void validateTransactionsData() {
        if (null == transactionsData || transactionsData.isEmpty()) {
            transactionsData = new ConcurrentHashMap<>();
        }
    }

    private String wrap(Double value) {
        if (!value.isInfinite()) {
            return BigDecimal.valueOf(value).setScale(2,RoundingMode.HALF_UP).toString();
        }
        return BigDecimal.ZERO.setScale(2,RoundingMode.HALF_UP).toString();
    }

    private long packTransactionTimeStamp(final Long transactionTimeStamp) {
        //Multithreaded request sometime send more than 1 request in the same millis precision, causing duplicate key,
        //packing transaction timestamp with a random generated number ensure high precision in generating more unique key.
        return transactionTimeStamp + (Double.valueOf(Math.random()*MILLIS_TO_PACK_SIZE)).longValue();
    }
}
