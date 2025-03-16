package com.syed.exchange_rate_service.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ExchangeRateCache {

    private Map<String, Double> exchangeRates = new HashMap<>();

    public void updateRates(Map<String, Double> rates) {
        this.exchangeRates.clear();
        this.exchangeRates.putAll(rates);
    }

    public Map<String, Double> getRates() {
        return new HashMap<>(exchangeRates);
    }

    public Double getRate(String currency) {
        return exchangeRates.get(currency.toUpperCase());
    }

    public boolean isEmpty() {
        return exchangeRates.isEmpty();
    }
}