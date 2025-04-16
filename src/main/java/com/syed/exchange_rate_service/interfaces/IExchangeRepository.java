package com.syed.exchange_rate_service.interfaces;

import java.util.Map;

public interface IExchangeRepository {
    void incrementRequestCount(String... currencies);
    Map<String, Integer> getCurrencyRequestCount();
}
