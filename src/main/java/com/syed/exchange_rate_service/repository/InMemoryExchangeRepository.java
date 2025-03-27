package com.syed.exchange_rate_service.repository;

import com.syed.exchange_rate_service.db.CurrencyRequestCount;
import com.syed.exchange_rate_service.interfaces.ExchangeRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryExchangeRepository implements ExchangeRepository {

    private final CurrencyRequestCount currencyRequestCount;

    public InMemoryExchangeRepository(CurrencyRequestCount currencyRequestCount) {
        this.currencyRequestCount = currencyRequestCount;
    }

    @Override
    public void incrementRequestCount(String... currencies) {
        currencyRequestCount.incrementRequestCount(currencies);
    }

    @Override
    public Map<String, Integer> getCurrencyRequestCount() {
        return currencyRequestCount.getCurrencyRequestCount();
    }
}
