package com.syed.exchange_rate_service.interfaces;

import java.util.Map;

public interface ExchangeRepository {
    void incrementRequestCount(String... currencies);
    Map<String, Integer> getCurrencyRequestCount();
}
