package com.syed.exchange_rate_service.dtos;

public record CurrencyConversionResponse(
        String fromCurrency,
        String toCurrency,
        double rate,
        double originalAmount,
        double convertedAmount) {
}
