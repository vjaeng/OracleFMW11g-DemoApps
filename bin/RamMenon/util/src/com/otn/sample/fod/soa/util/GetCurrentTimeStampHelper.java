package com.otn.sample.fod.soa.util;

/**
 * Simple class to get the timestamp back for the deployment plan naming, 
 * as they have to be uniquely named every time.
 */
public class GetCurrentTimeStampHelper 
{

    /**
     * Generates a *somewhat* unique id, based on the timestamp
     * @param args none
     */
    public static void main(String args[])
    {
        System.out.println((new StringBuilder()).append("_").
                        append(System.currentTimeMillis()).toString());
    }


}
