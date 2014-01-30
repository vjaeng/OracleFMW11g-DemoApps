package com.otn.sample.fod.soa.internalsupplier.test;

import java.util.HashMap;
import java.util.Map;

import oracle.soa.platform.component.spring.beans.IHeaderHelperBean;

/**
 * Simple test impl for the header handler bean
 */
public class MockHeaderHandlerImpl implements IHeaderHelperBean 
{

    private Map <String, String> mDummyHeaders =
        new HashMap <String, String>();

    /**
     * Get a headerproperty based on the key
     * @param key the key for the property
     * @return the value, or null if non existant
     */
    public String getHeaderProperty(String key) 
    {
        return mDummyHeaders.get(key);
    }

    /**
     * Set a header property, with key and value
     * @param key a unique, non null key
     * @param value a string based value
     */
    public void setHeaderProperty(String key, String value) 
    {
        mDummyHeaders.put(key, value);
    }
}
