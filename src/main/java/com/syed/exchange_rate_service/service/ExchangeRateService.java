package com.syed.exchange_rate_service.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.syed.exchange_rate_service.cache.ExchangeRateCache;
import com.syed.exchange_rate_service.dto.CurrencyPairResponse;
import com.syed.exchange_rate_service.dto.ExchangeRateResponse;
import com.syed.exchange_rate_service.model.ExchangeRate;
import com.syed.exchange_rate_service.model.xml.Envelope;
import com.syed.exchange_rate_service.model.xml.Rate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
public class ExchangeRateService {

    private final RestClient restClient;
    private final XmlMapper xmlMapper;
    private final ExchangeRateCache exchangeRateCache;

    private static final String BASE_CURRENCY = "EUR";
    private static final String ECB_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public ExchangeRateService(RestClient.Builder restClientBuilder, XmlMapper xmlMapper, ExchangeRateCache exchangeRateCache ) {
        this.restClient = restClientBuilder.baseUrl(ECB_URL).build();
        this.xmlMapper = xmlMapper;
        this.exchangeRateCache = exchangeRateCache;
    }

    public ExchangeRateResponse getExchangeRates() {
        String xmlResponse = restClient.get()
                .retrieve()
                .body(String.class);

        try {
            Envelope envelope =  xmlMapper.readValue(xmlResponse, Envelope.class);

            Map<String, Double> ratesMap = new HashMap<>();
            for (Rate rate : envelope.getExchangeRate().getDailyRate().getRates()) {
                ratesMap.put(rate.getCurrency(), rate.getRate());
            }
            exchangeRateCache.updateRates(ratesMap);

            return new ExchangeRateResponse(
                    envelope.getExchangeRate().getDailyRate().getTime(),
                    ratesMap
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML response", e);
        }
    }

    public ExchangeRate getDailyExchangeRatesAsJson() {
        if (exchangeRateCache.isEmpty()) {
            getExchangeRates();
        }
        return  new ExchangeRate(exchangeRateCache.getRates());
    }

    public CurrencyPairResponse getExchangeRateForCurrency(String toCurrency, String fromCurrency) {
        ExchangeRate exchangeRates =  getDailyExchangeRatesAsJson();

        if(!exchangeRates.containsCurrency(toCurrency) && !BASE_CURRENCY.equals(toCurrency) ) {
            throw new IllegalArgumentException("Unsupported currency: " + toCurrency);
        }

        if(fromCurrency.equals(BASE_CURRENCY)) {
            return new CurrencyPairResponse(fromCurrency, toCurrency, exchangeRates.getRate(toCurrency));
        }

        Double euroToFromCurrency = exchangeRates.getRate(fromCurrency);
        Double euroToToCurrency = BASE_CURRENCY.equals(toCurrency) ? 1.0 :exchangeRates.getRate(toCurrency);

        System.out.println(euroToFromCurrency);
        System.out.println(euroToToCurrency);

        if(euroToFromCurrency == null){
            throw new IllegalArgumentException("Unsupported currency 2: " + fromCurrency);
        }

        double rate = euroToToCurrency / euroToFromCurrency;
        double roundedRate = BigDecimal.valueOf(rate).setScale(4, RoundingMode.HALF_UP).doubleValue();

        return new CurrencyPairResponse(fromCurrency, toCurrency, roundedRate);
    }
}
