package com.syed.exchange_rate_service.model;

import java.util.Map;

public class ExchangeRate {
    private final Map<String, Double> rates;

    public ExchangeRate(Map<String, Double> rates) {
        this.rates = rates;
    }

    public Map<String, Double> getRates() {
        return rates;
    }

    public Double getRate(String currency) {
        return rates.get(currency);
    }

    public boolean containsCurrency(String currency) {
        return rates.containsKey(currency);
    }
}