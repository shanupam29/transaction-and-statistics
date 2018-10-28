package com.n26.service;

import com.n26.model.Statistics;
import org.springframework.stereotype.Service;

@Service
public interface StatisticsService {
    Statistics retrieveStatistics();
}
