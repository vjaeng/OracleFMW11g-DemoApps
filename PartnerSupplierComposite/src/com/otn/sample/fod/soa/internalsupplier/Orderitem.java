package com.otn.sample.fod.soa.internalsupplier;

import java.io.Serializable;
/**
 * Internal supplier format for Orderitems
 * 
 *                 !!! ATTENTION !!!
 * This POJO was used to generate the wsdl 
 *  (IInternalPartnerSupplierService.wsdl) - DO NOT MODIFY!
 *  
 * @author clemens utschig
 */
public class Orderitem implements Serializable
{
    
    private double mPrice = 0.0;
    private int mQuantity = 0;
    private String mProductId;
    
    public Orderitem () {
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
