/**
 * ATTENTION: 
 * This class is copyied from the ExternalPartnerSupplier Project. 
 *
 * !!!! DO NOT MODIFY !!!
 */
package com.otn.sample.fod.soa.externalps;

import java.io.Serializable;

public class OrderItem implements Serializable
{
    
    private double mPrice = 0.0;
    private int mQuantity = 0;
    private String mProductId;
    
    public OrderItem () {      
    }

    public final void setPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public final double getPrice() {
        return mPrice;
    }

    public final void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public final int getQuantity() {
        return mQuantity;
    }

    public final void setProductId(String mProductId) {
        this.mProductId = mProductId;
    }

    public final String getProductId() {
        return mProductId;
    }
}
