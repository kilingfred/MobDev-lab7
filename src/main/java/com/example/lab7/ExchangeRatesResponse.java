package com.example.lab7;

import java.util.List;

public class ExchangeRatesResponse {
    private String date;
    private String bank;
    private int baseCurrency;
    private String baseCurrencyLit;
    private List<Currency_model> exchangeRate;

    public List<Currency_model> getExchangeRate() {
        return exchangeRate;
    }
}
