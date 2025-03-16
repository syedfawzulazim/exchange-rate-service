package com.syed.exchange_rate_service.controller;

import com.syed.exchange_rate_service.dto.CurrencyPairResponse;
import com.syed.exchange_rate_service.dto.ExchangeRateResponse;
import com.syed.exchange_rate_service.service.ExchangeRateService;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/exchange")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/rates")
    public ExchangeRateResponse getAllExchangeRates() {
       return exchangeRateService.getExchangeRates();
    }

    @GetMapping("/rates/pair")
    public CurrencyPairResponse getRateForCurrencyPair(
            @RequestParam String toCurrency,
            @RequestParam String fromCurrency) {
        return exchangeRateService.getExchangeRateForCurrency(toCurrency.toUpperCase(), fromCurrency.toUpperCase());
    }

}
