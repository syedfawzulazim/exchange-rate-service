package com.syed.exchange_rate_service.cahce;

import com.syed.exchange_rate_service.cache.ExchangeRateCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRateCacheTest {

    private ExchangeRateCache cache;

    @BeforeEach
    public void setUp(){
        cache = new ExchangeRateCache();
    }

    @Test
    void testCacheInitiallyEmpty() {
        assertTrue(cache.isEmpty(), "Cache should be empty initially");
    }

    @Test
    void testUpdateRatesShouldAddRatesToCache(){
        // given
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.20);
        rates.put("GBP", 0.85);

        // when
        cache.updateRates(rates);

        // then
        Map<String, Double> cachedRates = cache.getRates();
        assertFalse(cachedRates.isEmpty(), "Cache should not be empty after update");
        assertEquals(1.20, cachedRates.get("USD"));
        assertEquals(0.85, cachedRates.get("GBP"));
        assertEquals(1.0, cachedRates.get("EUR"), "EUR should always be in cache with value 1.0");
    }

    @Test
    void testGetRatesReturnsCopyOfCache() {
        // given
        Map<String, Double> rates = new HashMap<>();
        rates.put("JPY", 130.5);
        cache.updateRates(rates);

        // when
        Map<String, Double> copyOfRates = cache.getRates();
        copyOfRates.put("TEST", 999.99); // Modifying copy, should not affect original

        // then
        assertFalse(cache.getRates().containsKey("TEST"), "Cache should remain unchanged");
    }

    @Test
    void testIsEmptyReturnsTrueWhenCacheIsCleared() {
        // given
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.1);
        cache.updateRates(rates);

        // when
        cache.updateRates(new HashMap<>());

        // then
        assertFalse(cache.isEmpty(), "Cache should still contain EUR");
        assertFalse(cache.getRates().containsKey("USD"), "USD should be removed");
        assertEquals(1.0, cache.getRates().get("EUR"), "EUR should still be present");
    }
}
