package com.example.lab7;

public class Currency_model {
    private String baseCurrency;
    private String currency;
    private float saleRateNB;
    private float purchaseRateNB;
    private float saleRate;
    private float purchaseRate;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public float getPurchaseRateNB() {
        return purchaseRateNB;
    }

    public String getCurrency() {
        return currency;
    }

    public float getSaleRate() {
        return saleRate;
    }

    public float getSaleRateNB() {
        return saleRateNB;
    }

    public float getPurchaseRate() {
        return purchaseRate;
    }
}
