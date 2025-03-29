package com.syed.exchange_rate_service.client;

import com.syed.exchange_rate_service.config.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ExchangeRateApiClient {

    private final RestClient restClient;

    public ExchangeRateApiClient(RestClient.Builder restClientBuilder, AppProperties appProperties){
        this.restClient = restClientBuilder.baseUrl(appProperties.getUrl()).build();
    }

    public String fetchExchangeRates(){
         return restClient.get()
                .retrieve()
                .body(String.class);
    }
}
