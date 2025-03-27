package com.syed.exchange_rate_service.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.syed.exchange_rate_service.cache.ExchangeRateCache;
import com.syed.exchange_rate_service.config.AppProperties;
import com.syed.exchange_rate_service.db.CurrencyRequestCount;
import com.syed.exchange_rate_service.dtos.CurrencyConversionResponse;
import com.syed.exchange_rate_service.dtos.CurrencyPairResponse;
import com.syed.exchange_rate_service.dtos.ExchangeRateResponse;
import com.syed.exchange_rate_service.interfaces.ExchangeRepository;
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
    private final ExchangeRepository exchangeRepository;
    private final String BASE_CURRENCY;

    public ExchangeRateService(
            RestClient.Builder restClientBuilder,
            XmlMapper xmlMapper,
            ExchangeRateCache exchangeRateCache,
            CurrencyRequestCount currencyRequestCount,
            AppProperties appProperties,
            ExchangeRepository exchangeRepository
    ) {
        this.restClient = restClientBuilder.baseUrl(appProperties.getUrl()).build();
        this.xmlMapper = xmlMapper;
        this.exchangeRateCache = exchangeRateCache;
        this.exchangeRepository = exchangeRepository;
        this.BASE_CURRENCY = appProperties.getBaseCurrency();
    }

    public ExchangeRateResponse fetchExchangeRates() {
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

    public CurrencyPairResponse getExchangeRateForCurrencyPair(String fromCurrency, String toCurrency) {
        double rate = getExchangeRate(fromCurrency, toCurrency);

        exchangeRepository.incrementRequestCount(fromCurrency, toCurrency);

        return new CurrencyPairResponse(fromCurrency, toCurrency, rate);
    }

    public Map<String, Integer> getSupporterCurrencyCount(){
        return exchangeRepository.getCurrencyRequestCount();
    }

    public CurrencyConversionResponse convertCurrency(double amount, String fromCurrency, String toCurrency){
        if(amount < 0){
            throw new IllegalArgumentException("Amount can not be negative: " +amount);
        }

        double rate = getExchangeRate(fromCurrency, toCurrency);

        double convertedAmount = amount * rate;
        double roundedAmount = BigDecimal.valueOf(convertedAmount).setScale(2, RoundingMode.HALF_UP).doubleValue();


        return new CurrencyConversionResponse(fromCurrency, toCurrency, rate, amount, roundedAmount);

    }

    private double getExchangeRate(String fromCurrency, String toCurrency){
        ExchangeRate exchangeRates =  getDailyExchangeRates();

        if (toCurrency.equals(fromCurrency)){
            throw new IllegalArgumentException("Can not convert same currency: " + toCurrency);
        }

        if((!exchangeRates.containsCurrency(toCurrency) || !exchangeRates.containsCurrency(fromCurrency))) {
            throw new IllegalArgumentException("Unsupported currency: " + toCurrency +" / "+ fromCurrency );
        }

        if(fromCurrency.equals(BASE_CURRENCY)) {
            return exchangeRates.getRate(toCurrency);
        }

        double euroToFromCurrency = exchangeRates.getRate(fromCurrency);
        double euroToToCurrency = exchangeRates.getRate(toCurrency);
        double rate = euroToToCurrency / euroToFromCurrency;

        return BigDecimal.valueOf(rate).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private ExchangeRate getDailyExchangeRates() {
        if (exchangeRateCache.isEmpty()) {
            fetchExchangeRates();
        }
        return  new ExchangeRate(exchangeRateCache.getRates());
    }
}
