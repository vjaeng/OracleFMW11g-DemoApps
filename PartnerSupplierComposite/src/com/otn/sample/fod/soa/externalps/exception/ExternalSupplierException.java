/**
 * ATTENTION: 
 * This class is copyied from ExternalPartnerSupplier Project. 
 *
 * !!!! DO NOT MODIFY !!!
 */
package com.otn.sample.fod.soa.externalps.exception;

/**
 * External Supplier Exception, raised in case someone sends unknown objects
 * @author clemens utschig
 */
public class ExternalSupplierException extends Exception 
{

    public ExternalSupplierException(String string) {
        super(string);
    }

    public ExternalSupplierException() {
        super();
    }
}
