package com.n26.controller;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.Instant;

@RestController("transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    /**
     * The endpoint to facilitate persist transaction information
     * containing amount and timstamp into the in-memory cache.
     *
     * Given - Transaction json object containing amount and timestamp in UTC.
     * @param transaction
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public ResponseEntity<Void> createTransaction(@NotNull @RequestBody Transaction transaction) {
        if (!validateJSON(transaction)) {
            return ResponseEntity.badRequest().build();
        }
        if(notAValidTransaction(transaction)){
            return ResponseEntity.unprocessableEntity().build();
        }
        //Condition to ensure the transaction will only be allowed to persist if transaction is less than 60s old.
        if (transaction.getLongTimestamp() > Instant.now().toEpochMilli() - TransactionService.TIME_OUT_DURATION) {
            transactionService.addTransaction(transaction);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/transaction")
                    .buildAndExpand(transaction.getLongTimestamp()).toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * The endpoint to clear all the transactions from the in-memory
     * cache.
     *
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public ResponseEntity<Void> deleteTransactions() {
        transactionService.clearTransactions();
        return ResponseEntity.noContent().build();
    }

    private Boolean validateJSON(final Transaction transaction) {
        if (transaction.getTimestamp() != null && transaction.getAmount() != null) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean notAValidTransaction(final Transaction transaction) {
        long timeNow = Instant.now().toEpochMilli();
        try {
            if (transaction.getLongTimestamp() > timeNow) {
                return Boolean.TRUE;
            }
            if (Double.valueOf(transaction.getAmount()).isNaN()) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }catch (Exception e) {
            return Boolean.TRUE;
        }
    }
}
