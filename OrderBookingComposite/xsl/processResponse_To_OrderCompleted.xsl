<?xml version="1.0" encoding="UTF-8" ?>
<?oracle-xsl-mapper
  <!-- SPECIFICATION OF MAP SOURCES AND TARGETS, DO NOT MODIFY. -->
  <mapSources>
    <source type="WSDL">
      <schema location="oramds:/apps/FusionOrderDemoShared/services/orderbooking/OrderBookingProcessor.wsdl"/>
      <rootElement name="processResponse" namespace="http://www.globalcompany.example.com/ns/OrderBookingService"/>
    </source>
  </mapSources>
  <mapTargets>
    <target type="XSD">
      <schema location="oramds:/apps/FusionOrderDemoShared/services/oracle/fodemo/storefront/entities/events/OrderEO.xsd"/>
      <rootElement name="OrderCompletedInfo" namespace="/oracle/fodemo/storefront/entities/events/schema/OrderEO"/>
    </target>
  </mapTargets>
  <!-- GENERATED BY ORACLE XSL MAPPER 11.1.1.0.0(build 081111.1524.2163) AT [SAT NOV 22 18:09:13 PST 2008]. -->
?>
<xsl:stylesheet version="1.0"
                xmlns:ns2="http://www.globalcompany.example.com/ns/OrderBookingService/rules"
                xmlns:bpws="http://schemas.xmlsoap.org/ws/2003/03/business-process/"
                xmlns:xpath20="http://www.oracle.com/XSL/Transform/java/oracle.tip.pc.services.functions.Xpath20"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:ns6="/oracle/fodemo/storefront/entities/events/schema/OrderEO"
                xmlns:ns3="http://www.globalcompany.example.com/ns/InternalWarehouse"
                xmlns:plnk="http://schemas.xmlsoap.org/ws/2003/05/partner-link/"
                xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                xmlns:ora="http://schemas.oracle.com/xpath/extension"
                xmlns:socket="http://www.oracle.com/XSL/Transform/java/oracle.tip.adapter.socket.ProtocolTranslator"
                xmlns:oraext="http://www.oracle.com/XSL/Transform/java/oracle.tip.pc.services.functions.ExtFunc"
                xmlns:ns4="commonj.sdo/xml"
                xmlns:dvm="http://www.oracle.com/XSL/Transform/java/oracle.tip.dvm.LookupValue"
                xmlns:hwf="http://xmlns.oracle.com/bpel/workflow/xpath"
                xmlns:mhdr="http://www.oracle.com/XSL/Transform/java/oracle.tip.mediator.service.common.functions.GetRequestHeaderExtnFunction"
                xmlns:med="http://schemas.oracle.com/mediator/xpath"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns5="commonj.sdo/java"
                xmlns:ids="http://xmlns.oracle.com/bpel/services/IdentityService/xpath"
                xmlns:ns1="/oracle/fodemo/storefront/store/queries/common/"
                xmlns:xdk="http://schemas.oracle.com/bpel/extension/xpath/function/xdk"
                xmlns:xref="http://www.oracle.com/XSL/Transform/java/oracle.tip.xref.xpath.XRefXPathFunctions"
                xmlns:ns0="commonj.sdo"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:client="http://www.globalcompany.example.com/ns/OrderBookingService"
                xmlns:ldap="http://schemas.oracle.com/xpath/extension/ldap"
                exclude-result-prefixes="xsi xsl ns2 ns3 plnk wsdl ns4 ns5 ns1 ns0 xsd client ns6 bpws xpath20 ora socket oraext dvm hwf mhdr med ids xdk xref ldap">
  <xsl:template match="/">
    <ns6:OrderCompletedInfo>
      <ns6:OrderId>
        <xsl:value-of select="/client:processResponse/client:result"/>
      </ns6:OrderId>
      <ns6:InstanceId>
        <xsl:value-of select='mhdr:getProperty("in.property.tracking.parentCompositeInstanceId")'/>
      </ns6:InstanceId>
    </ns6:OrderCompletedInfo>
  </xsl:template>
</xsl:stylesheet>
