package com.otn.sample.fod.soa;

//import com.sun.xml.ws.api.addressing.AddressingVersion;
//import com.sun.xml.ws.developer.WSBindingProvider;
//import com.sun.xml.ws.message.StringHeader;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import java.lang.reflect.Constructor;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.naming.Context;

import javax.xml.namespace.QName;

import oracle.fabric.common.NormalizedMessage;
import oracle.fabric.common.NormalizedMessageImpl;

import oracle.soa.api.JNDIDirectConnectionFactory;
import oracle.soa.api.XMLMessageFactory;
import oracle.soa.api.invocation.DirectConnection;
import oracle.soa.api.invocation.DirectConnectionFactory;
import oracle.soa.api.message.Message;
import oracle.soa.management.facade.Composite;
import oracle.soa.management.facade.CompositeInstance;
import oracle.soa.management.facade.ComponentInstance;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.facade.LocatorFactory;
import oracle.soa.management.facade.Sensor;
import oracle.soa.management.facade.Service;
import oracle.soa.management.facade.SensorData;

import oracle.soa.management.internal.facade.CompositeImpl;
import oracle.soa.management.util.ComponentInstanceFilter;
import oracle.soa.management.util.CompositeInstanceFilter;
import oracle.soa.management.util.Operator;
import oracle.soa.management.util.SensorFilter;

import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.Document;

import org.w3c.dom.Element;

import org.xml.sax.InputSource;

/**
 * Sample class to showcase the usage of the locator api to invoke an
 * exposed composite service, use SDOs and the usage of the jax-ws client stack.
 * <b>Note: </b> this class will ONLY function after the orderbooking composite was
 * deployed and the jax ws client generated (see bin/build.xml's build-ws-client task)
 * @author clemens utschig
 */
public class StandaloneSOAFodTests 
{
    // global build properties
    public static String STR_BLD_PROPS_NAME = "build.properties";
    // wl context key
    private static final String STR_WL_CTX_KEY = "java.naming.factory.initial";
        
    private static final String STR_BAMORDERBOOKING_COMPOSITE_NAME =
        "OrderBookingComposite";
    // service name, plus binding.adf in composite.xml
    private static final String STR_OB_SVCNAME =
        "orderprocessor_client_ep";
    // operation name on the orderbooking service 
    private static final String STR_OB_OPERATION = "process";
    // name of the payload part on the message
    private static final String STR_OB_PAYLOAD_PART_NAME = "payload";

    // qname of the service we want to invoke
    public static final QName OB_SERVICE_QNAME =
        new QName("http://www.globalcompany.example.com/ns/OrderBookingService",
              "orderprocessor_client_ep");    
    
//    // wsa version we will use for the uuid.
//    private static final AddressingVersion WS_ADDR_VER = AddressingVersion.W3C;

    /*
     * SDO composite
     */

    private static final String STR_SDO_COMPOSITE_NAME = "OrderSDOComposite";    
    // the operation name 
    private static final String STR_SDO_OPERATION = "getOrderInfoVOSDO";
    // service name, plus binding.adf in composite.xml
    private static final String STR_SDO_SVCNAME = 
        "StoreFrontService";
    private static final String STR_SDO_ORDER_XSD_URL_PF =
        "?XSD=../../../queries/common/OrderInfoVOSDO.xsd";
    private static final String STR_SDO_STOREFRONT_XSD_URL_PF =
        "?XSD=StoreFrontService.xsd";
    // domain separator
    private static final String STR_DOMAIN_SEPARATOR = "/";
    
    // domain name key
    private static final String STR_PARTITION_KEY = "soa.partition.name";
    
    // default version
    private static final String STR_VERSION = "!1.0";   

    /**
     * key strings that map into the global $FOD_HOME/bin/build.properties
     */
    public static final String STR_WLS_JDNI_KEY = "wls.mgd.server.url";    
    public static final String STR_MGDSERVER_HOST_KEY = "managed.server.host";
    public static final String STR_MGDSERVER_PORT_KEY = "managed.server.port";
    public static final String STR_MGDSERVER_RMIT_PORT_KEY = 
                                                "managed.server.rmi.port";    
    public static final String STR_USER_KEY = "server.user";
    public static final String STR_PASSWORD_KEY = "server.password";
    public static final String STR_USE_SOA_KEY = "soa.only.deployment";
    public static final String STR_SDO_SVC_URI = 
        "storefrontservice.contextUri.soa";    
    /*
     * internal flags
     */
    private static boolean mTestSDO = true;
//    private static boolean mTestWS = true;
    private static boolean mTestAPI = true;
    private static boolean mTestDirect = true;
    
   
    public static void main(String[] args) throws Exception 
    {
        
        // make this configurable so we can easily sneek in a different build
        // properties incarnation
        String overwritePropFileName =
            (String)System.getProperty("oracle.soa.test.propfile");
        if (overwritePropFileName != null &&
            overwritePropFileName.length() > 0)
          STR_BLD_PROPS_NAME = overwritePropFileName;
        
        /*
         * take the main build properties, this way we don't have 
         * to reconfigure anything
         */
        Properties props = new Properties();
        File globalPropsFile = new File(STR_BLD_PROPS_NAME);
        System.out.println("Loading props from: " + 
                           globalPropsFile.getAbsolutePath());
        props.load(new FileInputStream(globalPropsFile));

        StandaloneSOAFodTests invoker =
            new StandaloneSOAFodTests();
        
        
        if (args != null && args.length > 0) 
        {
//            if (!args[0].contains("ws"))    
//                mTestWS = false;
            if (!args[0].contains("api"))    
                mTestAPI = false;
            if (!args[0].contains("sdo"))    
                mTestSDO = false;
            if (!args[0].contains("direct"))    
                mTestDirect = false;
        }
        
        /*
         * test a post against the (bam)orderbooking
         */
        if (mTestAPI)
            invoker.testOrderBookingInvocation(props);
        
        /*
         * test a request with an SDO response against the sdo dummy impl
         */
        if (mTestSDO)
            invoker.testSDOCompositeInvocation(props);

//        /*
//         * test the orderbooking composite via jax ws proxy
//         */
//        if (mTestWS)
//            invoker.testOrderBookingCompositeInvokationViaWS(props);
        
        /*
         * test via diract binding
         */
        if (mTestDirect)
            invoker.testOrderBookingCompositeViaDirectBinding(props);
    }
    
    /**
     * Get a locator instance from the base properties
     * @param props the properties from ../build.properties
     * @return an instance of the Locator for soa-infra 
     * @throws Exception in case the locator cannot be created
     */
    protected Locator getLocatorFromProperties (Properties props) throws Exception 
    {
        Hashtable jndiProps = (Hashtable)getJNDIPropertiesFromBaseProps(props);
        System.out.println("Creating locator with properties: " + jndiProps);
        
        return LocatorFactory.createLocator(jndiProps);
    }
   
    /**
     * Helper method to parse the build props into a map
     * @param props the properties from the main build.properties
     * @return a map with key / value pairs
     */
    private Map <String, Object> getJNDIPropertiesFromBaseProps 
        (Properties props) 
    {
        Hashtable<String, Object> jndiProps = new Hashtable <String, Object>();
        String tmp_url = (String)props.get(STR_WLS_JDNI_KEY);
            tmp_url = tmp_url.replace(
                constructAntBasedPropName(STR_MGDSERVER_HOST_KEY), 
                (String)props.get(STR_MGDSERVER_HOST_KEY));
            tmp_url = tmp_url.replace(
                constructAntBasedPropName(STR_MGDSERVER_RMIT_PORT_KEY), 
                (String)props.get(STR_MGDSERVER_RMIT_PORT_KEY));

        jndiProps.put(Context.PROVIDER_URL, tmp_url);
        
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, 
                      props.get(STR_WL_CTX_KEY));
        jndiProps.put(Context.SECURITY_PRINCIPAL, 
                      props.get(STR_USER_KEY));
        jndiProps.put(Context.SECURITY_CREDENTIALS, 
                      props.get(STR_PASSWORD_KEY));
        return jndiProps;
    }
   
    /**
     * Test invocation of the orderbooking composite - via locator api
     * @param props the properties including which composite to invoke
     * @throws Exception
     */
    public void testOrderBookingInvocation (Properties props) 
        throws Exception
    {
        System.out.println(">>>>>>> Now testing Composite API invocation <<<<<< ");
        String compositeDN = getOrderBookingCompositeUri(props);
        System.out.println("Testing " + compositeDN + " via composite api");
        Locator locator = getLocatorFromProperties(props);
        
        String xml = new StringBuilder("<process ").append(
            "xmlns=\"http://www.globalcompany.example.com/ns/OrderBookingService\">")
            .append("<orderId>900</orderId></process>").toString();
        
        Composite composite = locator.lookupComposite(compositeDN);
        System.out.println("Composite = " + composite);
        System.out.println("Mode: " + composite.getMode() + " state: " + 
                           ((CompositeImpl)composite).getState());

        Service deliveryService = composite.getService(STR_OB_SVCNAME);
        System.out.println("DeliveryServce = " + deliveryService);

        // construct the normalized message and send to Oracle WLS
        String uuid = "uuid:" + UUID.randomUUID();
        NormalizedMessage nm = new NormalizedMessageImpl();
        // add a unigue id as conversation id, so we can find the guy back
        nm.addProperty(NormalizedMessage.PROPERTY_CONVERSATION_ID, uuid);
        nm.getPayload().put(STR_OB_PAYLOAD_PART_NAME, xml);

        deliveryService.post(STR_OB_OPERATION, nm);

        System.out.println(compositeDN + " executed! Id: " + uuid + "\r");
        // we need some time to start dehydrating stuff..
        Thread.sleep(15000);        
        /*
         * Retrieve instances, we are already on a composite, no need to set the
         * DN. the same thing can be done from the locator directly -
         * using locator.getCompositeInstances(<CompositeInstanceFilter>);
         */
        CompositeInstanceFilter filter = new CompositeInstanceFilter();
            filter.setMinCreationDate(
                new java.util.Date((System.currentTimeMillis() - 40000)));
            filter.setConversationId(uuid);
        // get composite instances by filter ..
        List <CompositeInstance> obInstances = composite.getInstances(filter);
        // we should find at least one..
        if (obInstances == null || obInstances.size() == 0)
            throw new Exception ("Could not find composite instance for " + 
                                 compositeDN);
        // for each of the returned composite instances..
        for (CompositeInstance instance : obInstances) 
        {
            System.out.println(" DN: " + instance.getCompositeDN() +
                               " > Instance: " + instance.getId() + 
                               " creation-date: " + instance.getCreationDate() +
                               " state (" + instance.getState() + "): " +
                               getStateAsString(instance.getState()));
            
            // setup a component filter    
            ComponentInstanceFilter cInstanceFilter =
                new ComponentInstanceFilter ();

            // get child component instances ..
            List <ComponentInstance> childComponentInstances = 
                instance.getChildComponentInstances(cInstanceFilter);
            for (ComponentInstance cInstance : childComponentInstances) 
            {
                System.out.println("  -> componentinstance: " + 
                                   cInstance.getComponentName() + " type: " + 
                                   cInstance.getServiceEngine().getEngineType()+
                                   " state: " + 
                                   getStateAsString(cInstance.getState()));
            }
            // retrieve composite sensors
            List <SensorData> sensorData = instance.getSensorData();
            for (SensorData data : sensorData) 
            {
                System.out.println(" -> Sensor: " + 
                                   data.getSensor().getName() + 
                                   " data: " + data.getData() + 
                                   " type: " +data.getSensor() +
                                   " on component: " + data.getComponentName());
            }
        }
                
        locator.close();
    }
    
    /**
     * In case soa.deployment = true, where we use a dummy composite impl -
     * that returns sdos - we test it with the below code, and call the
     * "getOrderInfoVOSDO" operation
     * @param props the central properties 
     * @throws Exception in case something goes wrong
     */
    public void testSDOCompositeInvocation (Properties props) throws Exception
    {
        System.out.println(">>>>>>> Now testing SDO <<<<<< ");
        String partName = "parameters";
        
        Locator locator = getLocatorFromProperties(props);
        int orderId = 900;
        String xml = new StringBuilder("<getOrderInfoVOSDO ")
            .append("xmlns=\"www.globalcompany.example.com/types/\">")
            .append("<orderId>").append(Integer.toString(orderId))
            .append("</orderId></getOrderInfoVOSDO>").toString();
        
        String compositeDN = null;
        if ("".equals(props.get(STR_USE_SOA_KEY)) || 
            "false".equals(props.get(STR_USE_SOA_KEY))) 
        {
            System.out.println("Cannot test sdo implementation, via SOA api");
            return;
        }
        else
        {
            compositeDN = (String)props.getProperty(STR_PARTITION_KEY) +
              STR_DOMAIN_SEPARATOR + STR_SDO_COMPOSITE_NAME + STR_VERSION;
        }
        Composite composite = locator.lookupComposite(compositeDN);
        if (composite == null)
            throw new Exception ("Cannot find composite with dn: " + 
                                 compositeDN);

        Service deliveryService = composite.getService(STR_SDO_SVCNAME);
        if (deliveryService == null)
            throw new Exception ("Cannot find service " + STR_SDO_SVCNAME + 
                                 " on composite " + composite.getDN() + "\n" +
                                 "Avalaible Services: " + composite.getServices());
        
        // construct the normalized message and send to Oracle WLS
        NormalizedMessage nm = new NormalizedMessageImpl();
        nm.getPayload().put(partName, xml);

        NormalizedMessage returnMsg = 
            deliveryService.request(STR_SDO_OPERATION, nm);

        System.out.println(compositeDN + " executed!<br>" + returnMsg);

        XMLElement elem = (XMLElement) returnMsg.getPayload().get(partName);
        // write out the a String writer
        StringWriter elemWriter = new StringWriter();
        elem.print(elemWriter);
        String sdoXmlAsString = elemWriter.getBuffer().toString();
        System.out.println("result: \n" + sdoXmlAsString);
                        
        /*
         * SDO test start here
         */
        XMLHelper xmlHelper = XMLHelper.INSTANCE;       

        String sdoServiceUrl = (String)props.get(STR_SDO_SVC_URI);
        sdoServiceUrl = sdoServiceUrl.replace(
          constructAntBasedPropName(STR_PARTITION_KEY), 
          (String)props.get(STR_PARTITION_KEY));

        /*
         * register the root type (getOrderInfoVOSDO)
         */
        String xsdStoreFrUrlSt = "http://" + props.get(STR_MGDSERVER_HOST_KEY) + ":" + 
            props.get(STR_MGDSERVER_PORT_KEY) + STR_DOMAIN_SEPARATOR + 
                sdoServiceUrl + STR_DOMAIN_SEPARATOR
                    + STR_SDO_STOREFRONT_XSD_URL_PF;
                        
        System.out.println("Retrieving schema for storefront response from: " +
                        xsdStoreFrUrlSt);                        
                        
        URL xsdStFUrl = new URL(xsdStoreFrUrlSt);
        InputStream xsdSfInputStream = xsdStFUrl.openStream();
        // register the type
        XSDHelper.INSTANCE.define(xsdSfInputStream, xsdStoreFrUrlSt);
        xsdSfInputStream.close();

        /*
         * register the real OrderInfoVOSDO type
         */
        
        // construct the url to the schema for the order sdo type
        String xsdOrderUrlSt = "http://" + props.get(STR_MGDSERVER_HOST_KEY) + ":" + 
            props.get(STR_MGDSERVER_PORT_KEY) + STR_DOMAIN_SEPARATOR + 
                sdoServiceUrl + STR_DOMAIN_SEPARATOR + STR_SDO_ORDER_XSD_URL_PF;
                        
        System.out.println("Retrieving schema for order from: " + xsdOrderUrlSt);                        
                        
        URL xsdOrderUrl = new URL(xsdOrderUrlSt);
        InputStream xsdOrderInputStream = xsdOrderUrl.openStream();
        // register the type
        XSDHelper.INSTANCE.define(xsdOrderInputStream, xsdOrderUrlSt);
        xsdOrderInputStream.close();

        // get the doc into an sdo understandable doc
        XMLDocument orderDoc = xmlHelper.load(sdoXmlAsString);
        
        /*
         * get the physical Do (named result, type = OrderInfoVOSDO)
         * the reason for getting the sub dataobject is because it's wrapped 
         * in the "getOrderInfoVOSDO" type, hence we need to ask for the child
         */
        DataObject orderDo = orderDoc.getRootObject().getDataObject("result");

        System.out.println("SDO type: " + orderDo.getType().getName());        

        int orderIdTest = orderDo.getInt("OrderId");    
        
        if (orderId != orderIdTest)
            throw new Exception ("Expected: " + orderIdTest + " actual: " + 
                                 orderId);
        locator.close();        
    }
    
//    /**
//     * Test the orderbooking composite via jax-ws proxy
//     * @param props the properties representing bin/build.properties
//     * @throws Exception in case something goes unexpected
//     */
//    public void testOrderBookingCompositeInvokationViaWS 
//        (Properties props) throws Exception
//    {
//        System.out.println(">>>>>>> Now testing via jax-ws <<<<<< ");
//        URL serviceUrl = new URL("http", 
//            props.getProperty(STR_MGDSERVER_HOST_KEY),
//            Integer.parseInt(props.getProperty(STR_MGDSERVER_PORT_KEY)),
//            getOrderBookingCompositeServiceWsdlUri(props));
//                
//        // initiate the client endpoint, generated through wsa 
//        
//        /*
//         * we do this through reflection so we don't have to bother about a
//         * class not found in jdev w/o proxy generated
//         */
//        Class orderprocessor_client_epDynClass =
//            Class.forName("com.example.globalcompany.ns.orderbookingservice.OrderprocessorClientEp");
//        Constructor opcConst = 
//            orderprocessor_client_epDynClass.getConstructor(
//                new Class[] {URL.class, QName.class}
//            );
//
//        /*
//         * create a new instance based on URL and service QName        
//         * this is equal to: 
//         *      new OrderprocessorClientEp(serviceUrl, OB_SERVICE_QNAME);
//         */ 
//        Object orderprocessor_client_epDyn =
//            opcConst.newInstance(new Object [] {serviceUrl, OB_SERVICE_QNAME});
//        
//        /*
//         * get the port
//         * this is equal to
//         *      OrderprocessorClientEp.getOrderProcessorPt();
//         */      
//        Method opcGetProcessorPt = 
//            orderprocessor_client_epDyn.getClass().
//                getMethod("getOrderProcessorPt");
//        
//        Object orderProcessor = 
//            opcGetProcessorPt.invoke(orderprocessor_client_epDyn);
//        
//        System.out.println("Proxy: " + orderProcessor);
//        
//        // cast the new Port to the WSBindingProvider so we can set headers
//        WSBindingProvider wsbp = (WSBindingProvider)orderProcessor;
//        String uuid = "uuid:" + UUID.randomUUID();
//
//        wsbp.setOutboundHeaders(new StringHeader(WS_ADDR_VER.messageIDTag,
//                                                 uuid));
//
//        /*
//         * get and invoke the process method        
//         * this is equal to:
//         *      OrderprocessorClientEp.getOrderProcessorPt().process("900");
//         */      
//        Method orderProcessorProcessM = 
//            orderProcessor.getClass().
//                getMethod("process", new Class [] {String.class});
//        orderProcessorProcessM.invoke(orderProcessor, new Object [] {"900"});
//        
//        Thread.sleep(10000);
//        // find the instance
//        Locator findCompositeLoc = getLocatorFromProperties(props);
//        System.out.println("Trying to find composite instance with id: " +
//                           uuid);
//        CompositeInstanceFilter filter = new CompositeInstanceFilter();
//            filter.setConversationId(uuid);
//        // get composite instances by filter ..
//        List <CompositeInstance> obInstances = 
//            findCompositeLoc.getCompositeInstances(filter);
//        // we should find one instance
//        if (obInstances == null || obInstances.size() == 0)
//            throw new Exception ("Could not find composite instance wit id: " + 
//                                 uuid);
//        System.out.println("Found instance via conversation id search ..");
//            
//        //Sensor: OrderProcessingStart data: Order 900
//        List <SensorFilter> sFilterList = new ArrayList <SensorFilter>();
//        SensorFilter sFilter = 
//            new SensorFilter("OrderProcessingStart" /* sensorname */,
//                             Sensor.SensorDataType.STRING /* type */,
//                             Operator.EQUALS /* operator for comparison */,
//                             "Order 900");
//            sFilterList.add(sFilter);
//            
//        filter = new CompositeInstanceFilter();
//            filter.setSensorFilter(sFilterList);
//            filter.setCompositeDN(getOrderBookingCompositeUri(props));
//            
//        obInstances = findCompositeLoc.getCompositeInstances(filter);
//        // we should find one instance
//        boolean foundInstance = false;
//        for (CompositeInstance instance : obInstances) 
//        {
//            if (instance.getConversationId().equals(uuid))
//            {
//                foundInstance = true;
//                break;   
//            }    
//        }
//        if (!foundInstance)
//            throw new Exception ("Could not find composite instance with id: " + 
//                                    uuid + " and sensorfilter: " + sFilter);            
//        System.out.println("Found instance via sensor value search");
//                                                        
//        findCompositeLoc.close();
//    }
    
    /**
     * Test the orderbooking composite via direct binding - this is a high
     * performance, non query, direct push api
     * @param props
     * @throws Exception in case the composite cannot be invoked
     */
    public void testOrderBookingCompositeViaDirectBinding (Properties props) 
        throws Exception
    {
        System.out.println(">>>>>>> Now testing via binding.direct <<<<<< ");
        String address = "soadirect:/" +
                getOrderBookingCompositeUri(props) + "/" + STR_OB_SVCNAME;
        System.out.println("URI call: --> " + address);
        String xml = new StringBuilder("<process ").append(
            "xmlns=\"http://www.globalcompany.example.com/ns/OrderBookingService\">")
            .append("<orderId>900</orderId></process>").toString();

        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(xml)));
        
        Element element = parser.getDocument().getDocumentElement();
        Map<String, Element> payload = new HashMap<String, Element>();
        payload.put(STR_OB_PAYLOAD_PART_NAME, element);
        Message<Element> xmlMessage = 
            XMLMessageFactory.getInstance().createMessage(payload);
        // get a handle on the jndi connection factory
        DirectConnectionFactory factory = JNDIDirectConnectionFactory.newInstance();
        Map<String, Object> jndiProperties = 
            getJNDIPropertiesFromBaseProps(props);
        // get a connection to the composite service
        DirectConnection conn = factory.createConnection(address, jndiProperties);
        String uuid = "direct: " + UUID.randomUUID();
        System.out.println("Setting up conversation id: " + uuid);
        xmlMessage.setProperty(Message.CONVERSATION_ID, uuid);
        conn.post(STR_OB_OPERATION, xmlMessage);
        
        // try to find the instance back.
        Locator findCompositeLoc = getLocatorFromProperties(props);
        CompositeInstanceFilter filter = new CompositeInstanceFilter();
            filter.setConversationId(uuid);
            
        List <CompositeInstance> obInstances = 
            findCompositeLoc.getCompositeInstances(filter);
        // we should find one instance
        boolean foundInstance = false;
        for (CompositeInstance instance : obInstances) 
        {
            if (instance.getConversationId().equals(uuid))
            {
                foundInstance = true;
                break;   
            }    
        }
        findCompositeLoc.close();
        if (!foundInstance)
            throw new Exception ("Could not find composite instance with id: " + 
                                    uuid);            
        System.out.println("Found instance via conversation id");        
        
    }
    
    protected String getOrderBookingCompositeUri (Properties props)
    {
        return (String)props.getProperty(STR_PARTITION_KEY) +
              STR_DOMAIN_SEPARATOR + STR_BAMORDERBOOKING_COMPOSITE_NAME + 
                STR_VERSION;
    }    

    protected String getOrderBookingCompositeServiceWsdlUri 
        (Properties props) 
    {    
        String serviceUri = getOrderBookingCompositeUri (props);
        
        return "/soa-infra/services/" + serviceUri + "/" + STR_OB_SVCNAME + "?wsdl";        
    }    
    
    private String getStateAsString (int state) 
    {
        // note that this is dependent on wheter the composite state is 
        // captured or not
        if (state == CompositeInstance.STATE_COMPLETED_SUCCESSFULLY)
            return ("success");
        else if (state == CompositeInstance.STATE_FAULTED)
            return ("faulted");
        else if (state == CompositeInstance.STATE_RECOVERY_REQUIRED)
            return ("recovery required");
        else if (state == CompositeInstance.STATE_RUNNING)
            return ("running");
        else if (state == CompositeInstance.STATE_STALE)
            return ("stale");
        else 
            return ("unknown");
    }
    
    /**
     * Needed for replacement of ant based properties
     * @param propName the propname
     * @return ${propName}
     */
    private String constructAntBasedPropName (String propName) 
    {
        return "${" + propName + "}";
    }    
}