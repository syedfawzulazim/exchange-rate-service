package com.syed.exchange_rate_service.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExchangeRateCache {

    private final Map<String, Double> exchangeRates = new HashMap<>();

    public void updateRates(Map<String, Double> rates) {
        this.exchangeRates.clear();
        // adds EURO
        rates.put("EUR", 1.0);
        this.exchangeRates.putAll(rates);
    }

    public Map<String, Double> getRates() {
        return new HashMap<>(exchangeRates);
    }

    public boolean isEmpty() {
        return exchangeRates.isEmpty();
    }
}