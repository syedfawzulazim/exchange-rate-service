package com.syed.exchange_rate_service.interfaces;

import com.syed.exchange_rate_service.dtos.CurrencyConversionResponse;
import com.syed.exchange_rate_service.dtos.CurrencyPairResponse;
import com.syed.exchange_rate_service.dtos.ExchangeRateResponse;

import java.util.Map;

public interface IExchangeRateService {
    ExchangeRateResponse fetchExchangeRates();

    CurrencyPairResponse getExchangeRateForCurrencyPair(String fromCurrency, String toCurrency);

    Map<String, Integer> getSupportedCurrencyCount();

    CurrencyConversionResponse convertCurrency(double amount, String fromCurrency, String toCurrency);
}
