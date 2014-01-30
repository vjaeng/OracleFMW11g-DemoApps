package com.otn.sample.fod.soa;

// external supplier
import com.otn.sample.fod.soa.externalps.OrderItem;
import com.otn.sample.fod.soa.externalps.exception.ExternalSupplierException;
// internal supplier
import com.otn.sample.fod.soa.internalsupplier.IInternalPartnerSupplier;
import com.otn.sample.fod.soa.internalsupplier.InternalSupplierMediator;
import com.otn.sample.fod.soa.internalsupplier.Orderitem;
import com.otn.sample.fod.soa.internalsupplier.exception.InternalSupplierException;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.naming.InitialContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Small testclass for the standalone case and for the composite service 
 * exposed as ejb
 * @author clemens utschig
 */
public class TestSpringComponents 
{
    // spring context name
    public static final String STR_APP_CTX_NAME = "test-spring-context.xml";
    // the context properties to be injected via spring
    public static final String STR_CTX_PROPS_NAME = "tmp_project.properties";    
    // internal partner supplier bean name
    private static final String STR_IPS_BEAN_NAME = "InternalPartnerSupplier";
    // test the ejb deployed composite
    private static final String STR_IPS_COMP_SVC_BEAN = 
      "IInternalCompositeSupplierService";
    // internal partner supplier bean name for the ejb
    private static final String STR_IPS_EJB_BEAN_NAME = 
        "InternalPartnerSupplierEJB";
    // internal partner supplier bean name for the negative testcase
    private static final String STR_IPS_FAIL_BEAN_NAME = 
        "FailingInternalPartnerSupplierMissingEPS";
    // internal partner supplier bean name for the negative testcase (2) - NPE
    private static final String STR_IPS_FAIL_BEAN_NAME_MISSING_QWR = 
        "FailingInternalPartnerSupplierMissingQwr";
    
    /**
     * Constructor, calling the test routings ..
     * @throws Exception in case something in the tests blows up
     */
    public TestSpringComponents() throws Exception 
    {
        ClassPathXmlApplicationContext factory = null;
        try
        {
            // get the bean factory from the context
            factory = new ClassPathXmlApplicationContext(STR_APP_CTX_NAME);
            // test the local mock bean via spring
            testLocalMockBean(factory);        
            // test the remote bean
            testRemoteEJBean(factory);        
            // test with wrong input, so we are expecting an exception ..
            // this would normally not occur at people go through the mediator.
            testWrongInput(factory);
            // test with a non injected reference (EPS)
            testFailingISE(factory);
            // test the ejb exposed service
            testRemoteEJBLookupOfCompositeService(factory);
        } catch (Throwable tl) 
        {
          System.out.println("Exception occured during spring testing: " + 
                             tl.getMessage());
          tl.printStackTrace();
          throw (Exception)tl;
        } finally 
        {
            if (factory != null)
                factory.close();
        }
        // if we end up here all tests succeeded.
        System.out.println("Spring based tests completed successfully ..");
    }

    /**
     * Test the local mock bean
     * @param factory the beanfactory
     * @throws Exception in case something goes wrong ..
     */
    public void testLocalMockBean (ClassPathXmlApplicationContext factory) 
        throws Exception 
    {
        // get the supplier bean
        IInternalPartnerSupplier supplier =
            (IInternalPartnerSupplier)factory.getBean(STR_IPS_BEAN_NAME);

        System.out.println("Local Supplier: " + supplier + 
                            " -> " + ((InternalSupplierMediator)supplier).
                            getExternalPartnerSupplier());
                           
        // execute some method on it.
        double price = supplier.getPriceForOrderItemList(null);
        if (price != 10.0)
            throw new Exception("Price is not 10.0");
        else
            System.out.println("> Got correct price from local supplier mock");
    }

    /**
     * Test the local bean with the ejb injected
     * @param factory the beanfactory
     * @throws Exception in case something goes wrong ..
     */
    public void testRemoteEJBean (ClassPathXmlApplicationContext factory) 
        throws Exception 
    {
        // get the supplier bean that is wired to an ejb in the backend
        IInternalPartnerSupplier remoteSupplier =
            (IInternalPartnerSupplier)factory.getBean(STR_IPS_EJB_BEAN_NAME);
        System.out.println("Remote supplier: " + remoteSupplier + 
                            " -> " + ((InternalSupplierMediator)remoteSupplier).
                            getExternalPartnerSupplier());
        List<Orderitem> items = createLegacyExternalOrderItems();
        
        // execute some method on it, this time with params.
        double price = remoteSupplier.getPriceForOrderItemList(items);
        if (price != 50.0)
            throw new Exception("Price is not 50.0 but: " + price);
        else 
            System.out.println("> Got correct price from ejb based supplier");
    }

    /**
     * Test the negative case - with wrong input
     * @param factory the beanfactory
     * @throws Exception in case something goes wrong ..
     */
    public void testWrongInput (ClassPathXmlApplicationContext factory) 
        throws Exception 
    {
        IInternalPartnerSupplier remoteSupplier =
            (IInternalPartnerSupplier)factory.getBean(STR_IPS_EJB_BEAN_NAME);
        System.out.println("Got remote supplier: " + remoteSupplier + 
                            " -> " + ((InternalSupplierMediator)remoteSupplier).
                            getExternalPartnerSupplier());
        Vector legacyItems = new Vector();
        // it's an untyped vector - so kind of bad luck..
        legacyItems.add("testWrongFormat");
        ExternalSupplierException extEx = null;
        try 
        {
            // this is just for testing - to get hold of the injected object
            // so we can try the inject wrong stuff..
            ((InternalSupplierMediator)remoteSupplier).
                getExternalPartnerSupplier().
                    getQuoteForItems(legacyItems);
        } catch (ExternalSupplierException testEx) {
            extEx = testEx;
            System.out.println(extEx.getMessage());
        }
        if (extEx == null)
            throw new Exception("Expected exception did not occur with wrong " +
                                "input..");        
        else
            System.out.println("> Got expected exception ..");
    }

    /**
     * Test the local bean without the ejb injected and w/o QWR injected
     * @param factory the beanfactory
     * @throws Exception in case something goes wrong ..
     */
    public void testFailingISE (ClassPathXmlApplicationContext factory) 
        throws Exception 
    {
        System.out.println("Testing with empty EPS and empty QWriter");
        IInternalPartnerSupplier remoteSupplier =
            (IInternalPartnerSupplier)factory.getBean(STR_IPS_FAIL_BEAN_NAME);
        
        InternalSupplierException iSETest = null;
        try 
        {
            remoteSupplier.getPriceForOrderItemList(null);
        } catch (Throwable iTest ) 
        {
            if (!(iTest instanceof InternalSupplierException))
                throw new Exception ("iTest not instance of " + 
                    InternalSupplierException.class + " Got: " + 
                                     iTest.getClass());
            
            iSETest = (InternalSupplierException)iTest;
        }
        if (iSETest == null)
            throw new Exception 
                ("Excepted exception of non found eps not found");
        System.out.println("Got exception: " + iSETest.getMessage());
        
        remoteSupplier =(IInternalPartnerSupplier)factory.getBean
                (STR_IPS_FAIL_BEAN_NAME_MISSING_QWR);
        
        // we expect a nullpointer here ..
        try 
        {
            iSETest = null;
            remoteSupplier.getPriceForOrderItemList(null);
        } catch (Throwable iTest ) 
        {
            if (!(iTest instanceof InternalSupplierException))
                throw new Exception ("iTest not instance of " + 
                    InternalSupplierException.class + " Got: " + iTest);
            
            iSETest = (InternalSupplierException)iTest;
        }
        if (iSETest == null)
            throw new Exception ("Excepted exception of failing qwr not found");
        
        if (!(iSETest.getCause() instanceof RuntimeException))
            throw new Exception ("Cause does not match, should be a RTE, is: " 
                                 + iSETest.getCause());
    }    
 
    /**
     * Test the composite service that exposes the spring component as ejb
     * @param factory the bean factory
     * @throws Exception in case something goes wrong
     */
    public void testRemoteEJBLookupOfCompositeService 
      (ClassPathXmlApplicationContext factory)  throws Exception 
    {

        // lookup a spring bean bound to the composite service 
        IInternalPartnerSupplier compositeSupplier = 
            (IInternalPartnerSupplier)factory.getBean(STR_IPS_COMP_SVC_BEAN);
        
        if (compositeSupplier == null)
            throw new Exception ("Could not find exposed composite supplier");
        System.out.println("Got composite service exposed as ejb: " + 
                           compositeSupplier);
        List<Orderitem> items = createLegacyExternalOrderItems();

        // execute some method on it, this time with params.
        double price = compositeSupplier.getPriceForOrderItemList(items);
        if (price != 50.0)
            throw new Exception("Price is not 50.0 but: " + price);
        else 
            System.out.println("> Got correct price from ejb based supplier");        
    }
    
    /**
     * Main method ran from outside ..
     * @param args none - noargs
     * @throws Exception in case some test breaks, and we bubble the exception
     */
    public static void main(String[] args) throws Exception 
    {
        // the spring context will suck these properties in - so there is no
        // value to let someone continue past here in case they are missing.
        // --> run from ant.
        if (Thread.currentThread().getContextClassLoader().
            getResource(STR_CTX_PROPS_NAME) == null)
            throw new Exception ("Cannot find context properties, please run "
                                 + " target test-spring-classes via ant.");
        TestSpringComponents testSpringComponents = new TestSpringComponents();
    }
    
    /**
     * Utility method to create a list of order items 
     * @return a list of items
     */
    private List<Orderitem> createLegacyExternalOrderItems () {
        List<Orderitem> items = new ArrayList<Orderitem>();
        Orderitem item = new Orderitem();
            item.setProductId("someProduct");
            item.setPrice(10.0);
            item.setQuantity(1);
        items.add(item);
        Orderitem item1 = new Orderitem();
            item1.setProductId("someProduct1");
            item1.setPrice(20.0);
            item1.setQuantity(2);
        items.add(item1);    
        return items;
    }
}
