package com.syed.exchange_rate_service.model.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class DailyRate {

    @JacksonXmlProperty(isAttribute = true)
    private String time;

    @JacksonXmlProperty(localName = "Cube")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Rate> rates;

    public DailyRate(){}

    public DailyRate(String time, List<Rate> rates) {
        this.time = time;
        this.rates = rates;
    }

    public String getTime() {
        return time;
    }

    public List<Rate> getRates() {
        return rates;
    }
}