package com.otn.sample.fod.soa.externalps;

import java.io.Serializable;

public class PriceQuote implements Serializable
{
    private double mPrice = 0.0;

    public PriceQuote() {
    }

    public PriceQuote (double pPrice) {
        mPrice = pPrice;
    }

    public final void setPrice(double pPrice) {
        this.mPrice = pPrice;
    }

    public final double getPrice() {
        return mPrice;
    }
}
