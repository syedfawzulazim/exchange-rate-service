package com.syed.exchange_rate_service.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.syed.exchange_rate_service.cache.ExchangeRateCache;
import com.syed.exchange_rate_service.client.ExchangeRateApiClient;
import com.syed.exchange_rate_service.config.AppProperties;
import com.syed.exchange_rate_service.dtos.CurrencyConversionResponse;
import com.syed.exchange_rate_service.dtos.CurrencyPairResponse;
import com.syed.exchange_rate_service.dtos.ExchangeRateResponse;
import com.syed.exchange_rate_service.interfaces.ExchangeRepository;

import com.syed.exchange_rate_service.model.xml.DailyRate;
import com.syed.exchange_rate_service.model.xml.Envelope;
import com.syed.exchange_rate_service.model.xml.ExchangeRate;
import com.syed.exchange_rate_service.model.xml.Rate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ExchangeRateServiceTest {

    @Mock
    private XmlMapper xmlMapper;

    @Mock
    private ExchangeRateCache exchangeRateCache;

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private ExchangeRateApiClient apiClient;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testFetchExchangeRates_Success() throws Exception{
        // given
        String xmlResponse = "<xml>mock response</xml>";
        Envelope envelope = new Envelope(
                new ExchangeRate(
                        new DailyRate("2023-10-01", Arrays.asList(
                                new Rate("USD", 1.2),
                                new Rate("GBP", 0.85)
                        ))
                )
        );

        when(apiClient.fetchExchangeRates()).thenReturn(xmlResponse);
        when(xmlMapper.readValue(xmlResponse, Envelope.class)).thenReturn(envelope);

        Map<String, Double> expectedRates = new HashMap<>();
        expectedRates.put("USD", 1.2);
        expectedRates.put("GBP", 0.85);

        // when
        ExchangeRateResponse response = exchangeRateService.fetchExchangeRates();

        // then
        verify(apiClient, times(1)).fetchExchangeRates();
        verify(xmlMapper, times(1)).readValue(xmlResponse, Envelope.class);
        verify(exchangeRateCache, times(1)).updateRates(expectedRates);

        assertEquals("2023-10-01", response.date());
        assertEquals(expectedRates, response.rates());
    }

    @Test
    void testFetchExchangeRates_XmlParsingFails() throws Exception {
        String xmlResponse = "<xml>invalid</xml>";
        when(apiClient.fetchExchangeRates()).thenReturn(xmlResponse);
        when(xmlMapper.readValue(xmlResponse, Envelope.class)).thenThrow(new RuntimeException("Parse error"));

        assertThrows(RuntimeException.class, () -> exchangeRateService.fetchExchangeRates());
    }

    @Test
    void testGetExchangeRateForCurrencyPair_NonBaseCurrencies() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.0);
        rates.put("USD", 1.2);
        rates.put("GBP", 0.85);
        when(exchangeRateCache.isEmpty()).thenReturn(false);
        when(exchangeRateCache.getRates()).thenReturn(rates);

        CurrencyPairResponse response = exchangeRateService.getExchangeRateForCurrencyPair("USD", "GBP");

        double expectedRate = 0.85 / 1.2;
        assertEquals(expectedRate, response.rate(), 0.0001);
        verify(exchangeRepository).incrementRequestCount("USD", "GBP");
    }

    @Test
    void testGetExchangeRateForCurrencyPair_SameCurrency() {
        Map<String, Double> rates = Map.of("EUR", 1.0);
        when(exchangeRateCache.isEmpty()).thenReturn(false);
        when(exchangeRateCache.getRates()).thenReturn(rates);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> exchangeRateService.getExchangeRateForCurrencyPair("EUR", "EUR"));
        assertEquals("Can not convert same currency: EUR", exception.getMessage());
    }

    @Test
    void testConvertCurrency_Success() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.0);
        rates.put("USD", 1.2);
        when(exchangeRateCache.isEmpty()).thenReturn(false);
        when(exchangeRateCache.getRates()).thenReturn(rates);

        CurrencyConversionResponse response =
                exchangeRateService.convertCurrency(100.0, "EUR", "USD");

        assertEquals("EUR", response.fromCurrency());
        assertEquals("USD", response.toCurrency());
        assertEquals(1.2, response.rate(), 0.0001);
        assertEquals(100.0, response.originalAmount(), 0.0001);
        assertEquals(120.0, response.convertedAmount(), 0.0001);
    }

    @Test
    void testConvertCurrency_NegativeAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> exchangeRateService.convertCurrency(-10.0, "EUR", "USD"));
        assertEquals("Amount can not be negative: -10.0", exception.getMessage());
    }

    @Test
    void testGetExchangeRateForCurrencyPair_UnsupportedCurrency() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("EUR", 1.0);
        when(exchangeRateCache.isEmpty()).thenReturn(false);
        when(exchangeRateCache.getRates()).thenReturn(rates);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> exchangeRateService.getExchangeRateForCurrencyPair("EUR", "JPY"));
        assertEquals("Unsupported currency: JPY / EUR", exception.getMessage());
    }

}