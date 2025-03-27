package com.syed.exchange_rate_service.dtos;

public record CurrencyPairResponse (
        String fromCurrency,
        String toCurrency,
        double rate) {

}
