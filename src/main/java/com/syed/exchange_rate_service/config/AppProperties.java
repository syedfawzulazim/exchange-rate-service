package com.syed.exchange_rate_service.config;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Validated
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    @NotEmpty
    private final String baseCurrency;
    @NotEmpty
    private final String ecb_url;

    public AppProperties(
            @Value("${app.currency.base}") String baseCurrency,
            @Value("${app.ecb.url}") String ecbUrl
    ) {
        this.baseCurrency = baseCurrency;
        this.ecb_url = ecbUrl;
    }

    public String getUrl() {
        return this.ecb_url;
    }

    public String getBaseCurrency(){
        return this.baseCurrency;
    }
}
