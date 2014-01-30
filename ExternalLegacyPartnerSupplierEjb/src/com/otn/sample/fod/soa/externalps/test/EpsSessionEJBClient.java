package com.otn.sample.fod.soa.externalps.test;


import com.otn.sample.fod.soa.externalps.IExternalPartnerSupplierService;
import com.otn.sample.fod.soa.externalps.OrderItem;
import com.otn.sample.fod.soa.externalps.exception.ExternalSupplierException;

import java.io.File;
import java.io.FileInputStream;

import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * More or less generated client stub to test the ejb. Can do a little bit more
 * though in terms if reading all configs off the common build props
 * @author clemens utschig
 */
public class EpsSessionEJBClient 
{
    // global (=default) bin/build.properties
    public static String STR_BLD_PROPS_NAME = "../bin/build.properties";
    
    /*
     * Keys to retrieve values from the property file
     */
    private static final String STR_WL_CTX_KEY = "java.naming.factory.initial";

    public static final String STR_WLS_JDNI_KEY = "wls.mgd.server.url";    
    // server user 
    public static final String STR_USER_KEY           = "server.user";
    // server password
    public static final String STR_PASSWORD_KEY       = "server.password";
    // server targets
    public static final String STR_SVR_TARGETS_KEY    = "server.targets";
    
    // the ejb jndi uri
    public static final String STR_EPS_EJB_URI        = 
        "partnersupplier.ejb.uri";
    // the jsca ejb uri
    public static final String STR_EPS_JSCA_URI       =
        "partnersupplier.jsca.ejb.uri";
    public static final String STR_USE_JSCA           = "use.jsca.ejb.impl";
    public static final String STR_MGDSERVER_HOST_KEY = "managed.server.host";
    public static final String STR_MGDSERVER_RMI_PORT_KEY = 
        "managed.server.rmi.port";    
    
    /*
     * was specific
     */
    public static final String STR_WAS_NODE_NAME_KEY  = "was.node";
    
    public static void main(String [] args) throws Exception
    {
        // make this configurable so we can easily sneek in a different build
        // properties incarnation
        if (args != null && args.length > 0)
          STR_BLD_PROPS_NAME = args[0];
        
        Properties props = loadPropsFromGlobalPropFile();
        
        String uri = null;
        // use jsca / or normal ejb
        boolean useJsca = 
            Boolean.valueOf(props.getProperty(STR_USE_JSCA)).booleanValue();
        if (useJsca)
            uri = (String)props.get(STR_EPS_JSCA_URI);
        else 
            uri = (String)props.get(STR_EPS_EJB_URI);

        if (props.get(STR_WAS_NODE_NAME_KEY) != null) 
        {
            uri = uri.replace(
                  constructAntBasedPropName(STR_WAS_NODE_NAME_KEY), 
                  (String)props.get(STR_WAS_NODE_NAME_KEY));
            uri = uri.replace(
                  constructAntBasedPropName(STR_SVR_TARGETS_KEY), 
                  (String)props.get(STR_SVR_TARGETS_KEY));
        }
        System.out.println("Testing " + (useJsca ? "jsca" : "ejb 3.0") + 
            " ctx: " + uri);

        final Context context = getInitialContext(props);

        // lookup the EJB 
        IExternalPartnerSupplierService supplier = 
            (IExternalPartnerSupplierService) context.lookup(uri);
        // create a bunch of order items
        Vector orderItems = createOrderItems();
    
        double price = 
            supplier.getQuoteForItems(orderItems).getPrice();
        System.out.println("Price: " + price);
        if (price != 50.0)
            throw new Exception("Price is not 50.0 (is " + price + ") " +
                "1*10.0 + 2*20 --> conversion failed!");
        
        ExternalSupplierException testEx = null;
        try 
        {
            // has to fail at runtime
            orderItems.add("bla");
            price = supplier.getQuoteForItems(orderItems).getPrice();
        }
        catch (ExternalSupplierException ex) 
        {
            testEx = ex;    
            System.out.println("got expected exception: " + 
                               testEx.getMessage());
        }
        if (testEx == null)
            throw new Exception ("EJB did not throw expected exception on wrong"
                                 + " vector type");
        context.close();
    }

    /**
     * Create an initial context from the passed in properties
     * @param props the properties off the global bin/build.properties
     * @return a configured initial context
     * @throws Exception in case the context could not be created.
     */
    private static Context getInitialContext(Properties props) throws Exception 
    {
        Hashtable env = new Hashtable();
        // WebLogic Server 10.x connection details
        env.put( Context.INITIAL_CONTEXT_FACTORY, props.get(STR_WL_CTX_KEY));
        // ant would do for us, here we need to do it ourselves
        String tmp_url = (String)props.get(STR_WLS_JDNI_KEY);
            tmp_url = tmp_url.replace(
                constructAntBasedPropName(STR_MGDSERVER_HOST_KEY), 
                (String)props.get(STR_MGDSERVER_HOST_KEY));
            tmp_url = tmp_url.replace(
                constructAntBasedPropName(STR_MGDSERVER_RMI_PORT_KEY), 
                (String)props.get(STR_MGDSERVER_RMI_PORT_KEY));
            
        env.put(Context.PROVIDER_URL, tmp_url);
        env.put(Context.SECURITY_PRINCIPAL, 
                      props.get(STR_USER_KEY));
        env.put(Context.SECURITY_CREDENTIALS, 
                      props.get(STR_PASSWORD_KEY));
        env.put("dedicated.connection", "true");
        return new InitialContext( env );
    }
    
    /**
     * Create a bunch of test order items
     * @return a Vector containing orderitems
     */
    private static Vector createOrderItems () 
    {
        Vector orderItems = new Vector(1);
        OrderItem item = new OrderItem();
            item.setProductId("clemensProduct");
            item.setPrice(10.0);
            item.setQuantity(1);
        orderItems.add(item);
        OrderItem item1 = new OrderItem();
            item1.setProductId("clemensProductOne");
            item1.setPrice(20.0);
            item1.setQuantity(2);
        orderItems.add(item1); 
        return orderItems;
    }
    
    private static Properties loadPropsFromGlobalPropFile() 
        throws Exception
    {
        Properties props = new Properties();
        File globalPropsFile = new File(STR_BLD_PROPS_NAME);
        System.out.println("Loading props from: " + 
                           globalPropsFile.getAbsolutePath());
        props.load(new FileInputStream(globalPropsFile));
        return props;
    }

    /**
     * Needed for replacement of ant based properties
     * @param propName the propname
     * @return ${propName}
     */
    private static String constructAntBasedPropName (String propName) 
    {
        return "${" + propName + "}";
    }    
        
}
