package com.syed.exchange_rate_service.db;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeDB {
    private final Map<String, Integer> currencyRequestCount = new ConcurrentHashMap<>();

    public Map<String, Integer> getCurrencyRequestCount() {
        return new HashMap<>(currencyRequestCount);
    }

    public void incrementRequestCount(String... currencies) {
        for(String currency: currencies){
            if(currency != null){
                currencyRequestCount.merge(currency, 1, Integer::sum);
            }
        }
    }
}
