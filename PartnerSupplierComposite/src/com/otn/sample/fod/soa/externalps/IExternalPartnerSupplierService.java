/**
 * ATTENTION: 
 * This class is the interface copyied from ExternalPartnerSupplier Project. 
 *
 * !!!! DO NOT MODIFY !!!
 */
package com.otn.sample.fod.soa.externalps;

import com.otn.sample.fod.soa.externalps.exception.ExternalSupplierException;

import java.util.Vector;

public interface IExternalPartnerSupplierService 
{
    /**
     * Get the quote for a set of order lines
     * @param pOrderLines the order lines via untyped vector, the perfect case
     * to use java for mediation
     * @return a somewhat "complex" PriceQuote object
     * @throws ExternalSupplierException in case an unknown object has been 
     *  passed
     */
    public PriceQuote getQuoteForItems (Vector pOrderLines)
        throws ExternalSupplierException;

}
