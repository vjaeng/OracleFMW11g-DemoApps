package com.otn.sample.fod.soa.wls;

import com.otn.sample.fod.soa.StandaloneSOAFodTests;

import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.ws.message.StringHeader;

import java.io.File;
import java.io.FileInputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.xml.namespace.QName;

import oracle.soa.management.facade.CompositeInstance;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.facade.Sensor;
import oracle.soa.management.util.CompositeInstanceFilter;
import oracle.soa.management.util.Operator;
import oracle.soa.management.util.SensorFilter;

public class WLSSpecificSoaTests extends StandaloneSOAFodTests 
{

  // wsa version we will use for the uuid.
  private static final AddressingVersion WS_ADDR_VER = AddressingVersion.W3C;

  public WLSSpecificSoaTests () {
    super ();
  }

  /**
   * Test the orderbooking composite via jax-ws proxy
   * @param props the properties representing bin/build.properties
   * @throws Exception in case something goes unexpected
   */
  public void testOrderBookingCompositeInvokationViaWS 
      (Properties props) throws Exception
  {
      System.out.println(">>>>>>> Now testing via jax-ws <<<<<< ");
      URL serviceUrl = new URL("http", 
          props.getProperty(STR_MGDSERVER_HOST_KEY),
          Integer.parseInt(props.getProperty(STR_MGDSERVER_PORT_KEY)),
          getOrderBookingCompositeServiceWsdlUri(props));
              
      // initiate the client endpoint, generated through wsa 
      
      /*
       * we do this through reflection so we don't have to bother about a
       * class not found in jdev w/o proxy generated
       */
      Class orderprocessor_client_epDynClass =
          Class.forName("com.example.globalcompany.ns.orderbookingservice.OrderprocessorClientEp");
      Constructor opcConst = 
          orderprocessor_client_epDynClass.getConstructor(
              new Class[] {URL.class, QName.class}
          );

      /*
       * create a new instance based on URL and service QName        
       * this is equal to: 
       *      new OrderprocessorClientEp(serviceUrl, OB_SERVICE_QNAME);
       */ 
      Object orderprocessor_client_epDyn =
          opcConst.newInstance(new Object [] 
              {serviceUrl, OB_SERVICE_QNAME});
      
      /*
       * get the port
       * this is equal to
       *      OrderprocessorClientEp.getOrderProcessorPt();
       */      
      Method opcGetProcessorPt = 
          orderprocessor_client_epDyn.getClass().
              getMethod("getOrderProcessorPt");
      
      Object orderProcessor = 
          opcGetProcessorPt.invoke(orderprocessor_client_epDyn);
      
      System.out.println("Proxy: " + orderProcessor);
      
      // cast the new Port to the WSBindingProvider so we can set headers
      WSBindingProvider wsbp = (WSBindingProvider)orderProcessor;
      String uuid = "uuid:" + UUID.randomUUID();

      wsbp.setOutboundHeaders(new StringHeader(WS_ADDR_VER.messageIDTag,
                                               uuid));

      /*
       * get and invoke the process method        
       * this is equal to:
       *      OrderprocessorClientEp.getOrderProcessorPt().process("900");
       */      
      Method orderProcessorProcessM = 
          orderProcessor.getClass().
              getMethod("process", new Class [] {String.class});
      orderProcessorProcessM.invoke(orderProcessor, new Object [] {"900"});
      
      Thread.sleep(10000);
      // find the instance
      Locator findCompositeLoc = getLocatorFromProperties(props);
      System.out.println("Trying to find composite instance with id: " +
                         uuid);
      CompositeInstanceFilter filter = new CompositeInstanceFilter();
          filter.setConversationId(uuid);
      // get composite instances by filter ..
      List <CompositeInstance> obInstances = 
          findCompositeLoc.getCompositeInstances(filter);
      // we should find one instance
      if (obInstances == null || obInstances.size() == 0)
          throw new Exception ("Could not find composite instance wit id: " + 
                               uuid);
      System.out.println("Found instance via conversation id search ..");
          
      //Sensor: OrderProcessingStart data: Order 900
      List <SensorFilter> sFilterList = new ArrayList <SensorFilter>();
      SensorFilter sFilter = 
          new SensorFilter("OrderProcessingStart" /* sensorname */,
                           Sensor.SensorDataType.STRING /* type */,
                           Operator.EQUALS /* operator for comparison */,
                           "Order 900");
          sFilterList.add(sFilter);
          
      filter = new CompositeInstanceFilter();
          filter.setSensorFilter(sFilterList);
          filter.setCompositeDN(getOrderBookingCompositeUri(props));
          
      obInstances = findCompositeLoc.getCompositeInstances(filter);
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
      if (!foundInstance)
          throw new Exception ("Could not find composite instance with id: " + 
                                  uuid + " and sensorfilter: " + sFilter);            
      System.out.println("Found instance via sensor value search");
                                                      
      findCompositeLoc.close();
  }    

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

      WLSSpecificSoaTests invoker =
          new WLSSpecificSoaTests();
      
      boolean mTestSDO = true;
      boolean mTestWS = true;
      boolean mTestAPI = true;
      boolean mTestDirect = true;
      
      if (args != null && args.length > 0) 
      {
          if (!args[0].contains("ws"))    
              mTestWS = false;
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

      /*
       * test the orderbooking composite via jax ws proxy
       */
      if (mTestWS)
          invoker.testOrderBookingCompositeInvokationViaWS(props);
      
      /*
       * test via diract binding
       */
      if (mTestDirect)
          invoker.testOrderBookingCompositeViaDirectBinding(props);
  }
  
}
