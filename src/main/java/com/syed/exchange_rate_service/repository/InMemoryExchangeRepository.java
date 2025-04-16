package com.syed.exchange_rate_service.repository;

import com.syed.exchange_rate_service.db.ExchangeDB;
import com.syed.exchange_rate_service.interfaces.IExchangeRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class InMemoryExchangeRepository implements IExchangeRepository {

    private final ExchangeDB db;

    public InMemoryExchangeRepository(ExchangeDB db) {
        this.db = db;
    }

    @Override
    public void incrementRequestCount(String... currencies) {
        db.incrementRequestCount(currencies);
    }

    @Override
    public Map<String, Integer> getCurrencyRequestCount() {
        return db.getCurrencyRequestCount();
    }
}
