package com.otn.sample.fod.soa.internalsupplier;

import com.otn.sample.fod.soa.internalsupplier.exception.InternalSupplierException;

import java.util.List;

/**
 * The interface for the spring based service, with a typed list.
 * 
 *                 !!! ATTENTION !!!
 * This interface was used to generate the wsdl 
 *  (IInternalPartnerSupplierService.wsdl) - DO NOT MODIFY!
 *  
 * @author clemens utschig
 */
public interface IInternalPartnerSupplier 
{
    /**
     * Get a price for a list of orderItems
     * @param pOrderItems the list of orderitems
     * @return the price
     */
    public double getPriceForOrderItemList(List<Orderitem> pOrderItems)
        throws InternalSupplierException;
    
}
