package com.syed.exchange_rate_service.client;

import com.syed.exchange_rate_service.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ExchangeRateApiClient.class)
class ExchangeRateApiClientTest {

    @Autowired
    private ExchangeRateApiClient exchangeRateApiClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private AppProperties appProperties;

    @Test
    void testFetchExchangeRates_Success() throws IOException {
        // given
        ClassPathResource responseResource = new ClassPathResource("exchange_rates.xml");

        // Expect a request to the correct URL and respond with mock data
        mockServer.expect(requestTo("http://example.com"))
                .andRespond(withSuccess(responseResource, MediaType.APPLICATION_XML));

        // when
        String response = exchangeRateApiClient.fetchExchangeRates();
        String expectedResponse = new String(responseResource.getInputStream().readAllBytes());

        // then
        assertEquals(expectedResponse, response);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AppProperties appProperties() {
            return new AppProperties("EUR", "http://example.com");
        }
    }
}
