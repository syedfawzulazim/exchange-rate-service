package com.syed.exchange_rate_service.dto;

public record CurrencyConversionResponse(
        String fromCurrency,
        String toCurrency,
        double rate,
        double originalAmount,
        double converterAmount) {
}
