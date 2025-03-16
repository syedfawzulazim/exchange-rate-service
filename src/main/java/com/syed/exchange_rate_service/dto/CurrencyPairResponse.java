package com.syed.exchange_rate_service.dto;

public record CurrencyPairResponse (String fromCurrency, String toCurrency, double rate) {
}
