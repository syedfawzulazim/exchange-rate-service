package com.syed.exchange_rate_service.controller;

import com.syed.exchange_rate_service.dto.CurrencyConversionResponse;
import com.syed.exchange_rate_service.dto.CurrencyPairResponse;
import com.syed.exchange_rate_service.dto.ExchangeRateResponse;
import com.syed.exchange_rate_service.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rates")
    public ExchangeRateResponse getAllExchangeRates() {
       return exchangeRateService.fetchExchangeRates();
    }

    @GetMapping("/rates/pair")
    public CurrencyPairResponse getRateForCurrencyPair(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency ) {
        return exchangeRateService.getExchangeRateForCurrencyPair(
                fromCurrency.toUpperCase(),
                toCurrency.toUpperCase()
        );
    }

    @GetMapping("/convert")
    public CurrencyConversionResponse getCurrencyConversion(
            @RequestParam double amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency ){
        return exchangeRateService.convertCurrency(
                amount,
                fromCurrency.toUpperCase(),
                toCurrency.toUpperCase()
        );
    }

    @GetMapping("/currency-request-count")
    public Map<String, Integer> getSupportedCurrencyRequestCount() {
        return exchangeRateService.getSupporterCurrencyCount();
    }
}
