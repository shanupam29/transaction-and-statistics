package com.n26.controller;

import com.n26.model.Statistics;
import com.n26.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * The endpoint for retrieving the statistics of the
     * transactions made in the last 60 seconds.
     *
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/statistics")
    public ResponseEntity<Statistics> retrieveStatistics() {
        return ResponseEntity.ok(statisticsService.retrieveStatistics());
    }
}