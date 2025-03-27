package com.syed.exchange_rate_service.dtos;

import java.util.Map;

public record ExchangeRateResponse(
        String date,
        Map<String, Double> rates) {
}
