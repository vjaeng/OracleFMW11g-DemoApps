<?xml version="1.0" encoding="UTF-8" ?>
<?oracle-xsl-mapper
  <!-- SPECIFICATION OF MAP SOURCES AND TARGETS, DO NOT MODIFY. -->
  <mapSources>
    <source type="WSDL">
      <schema location="../FulfillOrder.wsdl"/>
      <rootElement name="orderInfoVOSDO" namespace="/oracle/fodemo/storefront/store/queries/common/"/>
    </source>
  </mapSources>
  <mapTargets>
    <target type="WSDL">
      <schema location="../BAM_OrderDO.wsdl"/>
      <rootElement name="_OrderCollection" namespace="http://xmlns.oracle.com/bam"/>
    </target>
  </mapTargets>
  <!-- GENERATED BY ORACLE XSL MAPPER 11.1.1.0.0(build 081209.0200.2271) AT [TUE DEC 09 11:50:02 PST 2008]. -->
?>
<xsl:stylesheet version="1.0"
                xmlns:bpws="http://schemas.xmlsoap.org/ws/2003/03/business-process/"
                xmlns:xpath20="http://www.oracle.com/XSL/Transform/java/oracle.tip.pc.services.functions.Xpath20"
                xmlns:imp1="http://xmlns.oracle.com/bam"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:plt="http://schemas.xmlsoap.org/ws/2003/05/partner-link/"
                xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                xmlns:ora="http://schemas.oracle.com/xpath/extension"
                xmlns:socket="http://www.oracle.com/XSL/Transform/java/oracle.tip.adapter.socket.ProtocolTranslator"
                xmlns:ns3="http://xmlns.oracle.com/adf/svc/errors/"
                xmlns:oraext="http://www.oracle.com/XSL/Transform/java/oracle.tip.pc.services.functions.ExtFunc"
                xmlns:tns="http://xmlns.oracle.com/WebLogicFusionOrderDemo/OrderBookingComposite/FulfillOrder"
                xmlns:ns1="commonj.sdo/xml"
                xmlns:dvm="http://www.oracle.com/XSL/Transform/java/oracle.tip.dvm.LookupValue"
                xmlns:ns2="http://xmlns.oracle.com/adf/svc/types/"
                xmlns:hwf="http://xmlns.oracle.com/bpel/workflow/xpath"
                xmlns:mhdr="http://www.oracle.com/XSL/Transform/java/oracle.tip.mediator.service.common.functions.GetRequestHeaderExtnFunction"
                xmlns:med="http://schemas.oracle.com/mediator/xpath"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns4="commonj.sdo/java"
                xmlns:ids="http://xmlns.oracle.com/bpel/services/IdentityService/xpath"
                xmlns:inp1="/oracle/fodemo/storefront/store/queries/common/"
                xmlns:xdk="http://schemas.oracle.com/bpel/extension/xpath/function/xdk"
                xmlns:xref="http://www.oracle.com/XSL/Transform/java/oracle.tip.xref.xpath.XRefXPathFunctions"
                xmlns:ns0="commonj.sdo"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:ns5="http://xmlns.oracle.com/pcbpel/adapter/bam/WebLogicFusionOrderDemo/OrderBookingComposite/BAM_OrderDO/"
                xmlns:ldap="http://schemas.oracle.com/xpath/extension/ldap"
                exclude-result-prefixes="xsi xsl wsdl ns3 tns ns1 ns2 ns4 inp1 ns0 xsd imp1 plt ns5 bpws xpath20 ora socket oraext dvm hwf mhdr med ids xdk xref ldap">
  <xsl:template match="/">
    <imp1:_OrderCollection>
      <imp1:_Order>
        <imp1:_OrderID>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderId"/>
        </imp1:_OrderID>
        <imp1:_CustomerID>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:CustomerId"/>
        </imp1:_CustomerID>
        <imp1:_OrderDate>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderDate"/>
        </imp1:_OrderDate>
        <imp1:_OrderStatus>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderStatusCode"/>
        </imp1:_OrderStatus>
        <imp1:_OrderPrice>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderTotal"/>
        </imp1:_OrderPrice>
      </imp1:_Order>
    </imp1:_OrderCollection>
  </xsl:template>
</xsl:stylesheet>
