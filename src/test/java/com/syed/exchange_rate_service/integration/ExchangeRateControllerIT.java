package com.syed.exchange_rate_service.integration;

import com.syed.exchange_rate_service.dtos.CurrencyConversionResponse;
import com.syed.exchange_rate_service.dtos.CurrencyPairResponse;
import com.syed.exchange_rate_service.dtos.ExchangeRateResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Exchange Rate API Integration Tests")
class ExchangeRateControllerIT extends BaseIntegrationTest {

    @Nested
    @DisplayName("GET /api/exchange/rates")
    class GetAllExchangeRatesTests {
        
        @Test
        @DisplayName("Should successfully fetch all exchange rates")
        void shouldFetchAllExchangeRates() throws Exception {
            // given
            ExchangeRateResponse mockResponse = new ExchangeRateResponse(
                "2024-03-31",
                Map.of("USD", 1.08, "GBP", 0.85, "JPY", 161.50)
            );
            when(exchangeRateService.fetchExchangeRates()).thenReturn(mockResponse);

            // when & then
            mockMvc.perform(get("/api/exchange/rates")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.date").value("2024-03-31"))
                    .andExpect(jsonPath("$.rates.USD").value(1.08))
                    .andExpect(jsonPath("$.rates.GBP").value(0.85))
                    .andExpect(jsonPath("$.rates.JPY").value(161.50));
        }
    }

    @Nested
    @DisplayName("GET /api/exchange/rates/pair")
    class GetCurrencyPairRateTests {
        
        @Test
        @DisplayName("Should successfully fetch rate for valid currency pair")
        void shouldFetchRateForValidCurrencyPair() throws Exception {
            // given
            when(exchangeRateService.getExchangeRateForCurrencyPair("USD", "EUR"))
                    .thenReturn(new CurrencyPairResponse("USD", "EUR", 0.85));

            // when & then
            mockMvc.perform(get("/api/exchange/rates/pair")
                    .param("fromCurrency", "USD")
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fromCurrency").value("USD"))
                    .andExpect(jsonPath("$.toCurrency").value("EUR"))
                    .andExpect(jsonPath("$.rate").value(0.85));
        }

        @Test
        @DisplayName("Should return 400 for invalid currency code length")
        void shouldReturn400ForInvalidCurrencyCodeLength() throws Exception {
            // when & then
            mockMvc.perform(get("/api/exchange/rates/pair")
                    .param("fromCurrency", "USDD")  // Invalid length
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for empty currency code")
        void shouldReturn400ForEmptyCurrencyCode() throws Exception {
            // when & then
            mockMvc.perform(get("/api/exchange/rates/pair")
                    .param("fromCurrency", "")
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for same currency conversion")
        void shouldReturn400ForSameCurrencyConversion() throws Exception {
            // given
            when(exchangeRateService.getExchangeRateForCurrencyPair("EUR", "EUR"))
                    .thenThrow(new IllegalArgumentException("Can not convert same currency: EUR"));

            // when & then
            mockMvc.perform(get("/api/exchange/rates/pair")
                    .param("fromCurrency", "EUR")
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Can not convert same currency: EUR"));
        }
    }

    @Nested
    @DisplayName("GET /api/exchange/convert")
    class CurrencyConversionTests {
        
        @Test
        @DisplayName("Should successfully convert currency")
        void shouldConvertCurrency() throws Exception {
            // given
            when(exchangeRateService.convertCurrency(100, "USD", "EUR"))
                    .thenReturn(new CurrencyConversionResponse("USD", "EUR", 0.85, 100, 85.0));

            // when & then
            mockMvc.perform(get("/api/exchange/convert")
                    .param("amount", "100")
                    .param("fromCurrency", "USD")
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.fromCurrency").value("USD"))
                    .andExpect(jsonPath("$.toCurrency").value("EUR"))
                    .andExpect(jsonPath("$.rate").value(0.85))
                    .andExpect(jsonPath("$.originalAmount").value(100))
                    .andExpect(jsonPath("$.convertedAmount").value(85.0));
        }

        @Test
        @DisplayName("Should return 400 for negative amount")
        void shouldReturn400ForNegativeAmount() throws Exception {
            // when & then
            mockMvc.perform(get("/api/exchange/convert")
                    .param("amount", "-100")
                    .param("fromCurrency", "USD")
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for zero amount")
        void shouldReturn400ForZeroAmount() throws Exception {
            // when & then
            mockMvc.perform(get("/api/exchange/convert")
                    .param("amount", "0")
                    .param("fromCurrency", "USD")
                    .param("toCurrency", "EUR")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for unsupported currency")
        void shouldReturn400ForUnsupportedCurrency() throws Exception {
            // given
            when(exchangeRateService.convertCurrency(100, "EUR", "XYZ"))
                    .thenThrow(new IllegalArgumentException("Unsupported currency: XYZ / EUR"));

            // when & then
            mockMvc.perform(get("/api/exchange/convert")
                    .param("amount", "100")
                    .param("fromCurrency", "EUR")
                    .param("toCurrency", "XYZ")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Unsupported currency: XYZ / EUR"));
        }
    }

    @Nested
    @DisplayName("GET /api/exchange/currency-request-count")
    class CurrencyRequestCountTests {
        
        @Test
        @DisplayName("Should successfully fetch currency request counts")
        void shouldFetchCurrencyRequestCounts() throws Exception {
            // given
            when(exchangeRateService.getSupportedCurrencyCount())
                    .thenReturn(Map.of("USD", 5, "EUR", 3, "GBP", 2));

            // when & then
            mockMvc.perform(get("/api/exchange/currency-request-count")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.USD").value(5))
                    .andExpect(jsonPath("$.EUR").value(3))
                    .andExpect(jsonPath("$.GBP").value(2));
        }

        @Test
        @DisplayName("Should return empty map when no requests")
        void shouldReturnEmptyMapWhenNoRequests() throws Exception {
            // given
            when(exchangeRateService.getSupportedCurrencyCount())
                    .thenReturn(Map.of());

            // when & then
            mockMvc.perform(get("/api/exchange/currency-request-count")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }
}