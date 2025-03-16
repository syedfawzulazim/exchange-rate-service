package com.syed.exchange_rate_service.model.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ExchangeRate {

    @JacksonXmlProperty(localName = "Cube")
    private DailyRate dailyRate;

    public DailyRate getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(DailyRate dailyRate) {
        this.dailyRate = dailyRate;
    }
}