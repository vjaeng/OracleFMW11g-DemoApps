package com.otn.sample.fod.soa.externalps.test;

import com.otn.sample.fod.soa.externalps.IExternalPartnerSupplierService;
import com.otn.sample.fod.soa.externalps.PriceQuote;
import com.otn.sample.fod.soa.externalps.exception.ExternalSupplierException;

import java.util.Vector;

/**
 *  Mockclass for the EJB from the ExternalPartnerSupplier project
 *  @author clemens utschig
 */
public class MockExternalPartnerSupplierTest implements 
    IExternalPartnerSupplierService 
{

    /**
     * In case you wonder, the idea here is to use a jdk 1.4 syntax, w/o
     * generics support, hence something that cannot be easily converted into
     * xml w/o annotations.
     */
    @Override
    public PriceQuote getQuoteForItems(Vector pOrderLines) 
        throws ExternalSupplierException
    {
        return new PriceQuote(10.0);
    }
}
