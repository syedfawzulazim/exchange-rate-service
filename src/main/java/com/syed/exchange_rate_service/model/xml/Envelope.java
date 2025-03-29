package com.syed.exchange_rate_service.model.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "Envelope", namespace = "http://www.ecb.int/vocabulary/2002-08-01/eurofxref")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Envelope {

    @JacksonXmlProperty(localName = "Cube")
    private ExchangeRate exchangeRate;

    public Envelope(){}

    public Envelope(ExchangeRate exchangeRate){
        this.exchangeRate = exchangeRate;
    }

    public ExchangeRate getExchangeRate() {
        return exchangeRate;
    }
}