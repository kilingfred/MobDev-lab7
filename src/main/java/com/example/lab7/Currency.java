package com.example.lab7;

public class Currency {
    String name;
    float price1;
    float price2;
    float price3;

    public void setPrice1(float price1) {
        this.price1 = price1;
    }

    public void setPrice2(float price2) {
        this.price2 = price2;
    }

    public void setPrice3(float price3) {
        this.price3 = price3;
    }

    String getName(){
        return this.name;
    }

    float getPrice1(){
        return this.price1;
    }
    float getPrice2(){return this.price2;}
    float getPrice3(){return this.price3;}

    Currency(String name){
        this.name = name;
        this.price1 = 0;
        this.price2 = 0;
        this.price3 = 0;
    }

    Currency(String name, float salesRate, float purchaseRate, float salesrateNB) {
        this.name = name;
        this.price1 = salesRate;
        this.price2 = purchaseRate;
        this.price3 = salesrateNB;
    }
}
