package com.otn.sample.fod.soa.internalsupplier.test;

import java.util.logging.Level;

import java.util.logging.Logger;

import oracle.soa.platform.component.spring.beans.ILoggerBean;

/**
 * Simple implementation of the IloggerBean interface, so we can mock it during
 * tests
 */
public class MockLoggerImpl implements ILoggerBean 
{
    Logger mlogger = Logger.getLogger(MockLoggerImpl.class.getName());

    /**
     * @see oracle.soa.platform.component.spring.beans.ILoggerBean#log(String)
     */
    public void log(String string) 
    {
        mlogger.log(Level.INFO, string);
    }

    /**
     * @see oracle.soa.platform.component.spring.beans.ILoggerBean#log(Level, String)
     */
    public void log(Level level, String string) 
    {
        mlogger.log(level, string);        
    }

    /**
     * @see oracle.soa.platform.component.spring.beans.ILoggerBean#log(Level, String, Throwable)
     */
    public void log(Level level, String string, Throwable throwable) 
    {
        mlogger.log(level, string, throwable);      
    }
}
