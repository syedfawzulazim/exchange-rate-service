package com.syed.exchange_rate_service.client;

import com.syed.exchange_rate_service.config.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ExchangeRateApiClient {
    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateApiClient.class);

    private final RestClient restClient;

    public ExchangeRateApiClient(AppProperties appProperties){
        this.restClient = RestClient.builder().baseUrl(appProperties.getUrl()).build();
    }

    public String fetchExchangeRates(){
        try {
            return restClient.get()
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException e) {
            logger.error("API returned an error: {}", e.getStatusCode(), e);
            throw new RuntimeException("Failed to fetch exchange rates: " + e.getStatusCode());
        } catch (RestClientException e) {
            logger.error("Network error while fetching exchange rates", e);
            throw new RuntimeException("Failed to fetch exchange rates due to network issue.");
        }
    }
}
