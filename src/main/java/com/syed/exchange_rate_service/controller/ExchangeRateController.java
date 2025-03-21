package com.syed.exchange_rate_service.controller;

import com.syed.exchange_rate_service.dto.CurrencyConversionResponse;
import com.syed.exchange_rate_service.dto.CurrencyPairResponse;
import com.syed.exchange_rate_service.dto.ExchangeRateResponse;
import com.syed.exchange_rate_service.service.ExchangeRateService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/exchange")
@Validated
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
            @RequestParam @NotBlank @Size(min = 3, max = 3)
            String fromCurrency,
            @RequestParam @NotBlank @Size(min = 3, max = 3)
            String toCurrency) {
        return exchangeRateService.getExchangeRateForCurrencyPair(
                fromCurrency.toUpperCase(),
                toCurrency.toUpperCase()
        );
    }

    @GetMapping("/convert")
    public CurrencyConversionResponse getCurrencyConversion(
            @RequestParam @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
            double amount,
            @RequestParam @NotBlank @Size(min = 3, max = 3)
            String fromCurrency,
            @RequestParam @NotBlank @Size(min = 3, max = 3)
            String toCurrency) {
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
