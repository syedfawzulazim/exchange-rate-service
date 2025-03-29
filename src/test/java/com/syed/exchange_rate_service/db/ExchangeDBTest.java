package com.syed.exchange_rate_service.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class ExchangeDBTest {

    private ExchangeDB exchangeDB;

    @BeforeEach
    void setUp() {
        exchangeDB = new ExchangeDB();
    }

    @Test
    void testGetCurrencyRequestCountInitiallyEmpty() {
        Map<String, Integer> currencyCounts = exchangeDB.getCurrencyRequestCount();
        assertTrue(currencyCounts.isEmpty(), "Currency request count map should be empty initially");
    }

    @Test
    void testIncrementRequestCountForSingleCurrency() {
        exchangeDB.incrementRequestCount("USD");

        Map<String, Integer> currencyCounts = exchangeDB.getCurrencyRequestCount();
        assertEquals(1, currencyCounts.get("USD"), "USD should have been incremented to 1");
    }

    @Test
    void testIncrementRequestCountForMultipleCurrencies() {
        exchangeDB.incrementRequestCount("USD", "EUR", "GBP");

        Map<String, Integer> currencyCounts = exchangeDB.getCurrencyRequestCount();
        assertEquals(1, currencyCounts.get("USD"), "USD should have been incremented to 1");
        assertEquals(1, currencyCounts.get("EUR"), "EUR should have been incremented to 1");
        assertEquals(1, currencyCounts.get("GBP"), "GBP should have been incremented to 1");
    }

    @Test
    void testIncrementRequestCountMultipleTimesForSameCurrency() {
        exchangeDB.incrementRequestCount("USD", "USD");

        Map<String, Integer> currencyCounts = exchangeDB.getCurrencyRequestCount();
        assertEquals(2, currencyCounts.get("USD"), "USD should have been incremented to 2");
    }

    @Test
    void testIncrementRequestCountDoesNotIncrementForNull() {
        exchangeDB.incrementRequestCount("USD", null, "EUR");

        Map<String, Integer> currencyCounts = exchangeDB.getCurrencyRequestCount();
        assertNull(currencyCounts.get(null), "Null should not be a key in the currency request map");
        assertEquals(1, currencyCounts.get("USD"), "USD should be incremented to 1");
        assertEquals(1, currencyCounts.get("EUR"), "EUR should be incremented to 1");
    }

    @Test
    void testGetCurrencyRequestCountReturnsCopy() {
        exchangeDB.incrementRequestCount("USD");

        Map<String, Integer> currencyCounts = exchangeDB.getCurrencyRequestCount();
        currencyCounts.put("USD", 999);

        assertEquals(1, exchangeDB.getCurrencyRequestCount().get("USD"), "Original map should remain unchanged");
    }
}
