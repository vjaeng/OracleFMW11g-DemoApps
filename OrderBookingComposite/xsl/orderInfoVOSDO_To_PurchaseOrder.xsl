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
      <schema location="../USPSShipment.wsdl"/>
      <rootElement name="PurchaseOrder" namespace="http://www.globalcompany.example.com/ns/legacyOrder"/>
    </target>
  </mapTargets>
  <!-- GENERATED BY ORACLE XSL MAPPER 11.1.1.0.0(build 080902.2000.1881) AT [TUE SEP 16 22:11:56 PDT 2008]. -->
?>
<xsl:stylesheet version="1.0"
                xmlns:bpws="http://schemas.xmlsoap.org/ws/2003/03/business-process/"
                xmlns:xpath20="http://www.oracle.com/XSL/Transform/java/oracle.tip.pc.services.functions.Xpath20"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:plt="http://schemas.xmlsoap.org/ws/2003/05/partner-link/"
                xmlns:ns5="http://xmlns.oracle.com/pcbpel/adapter/file/WebLogicFusionOrderDemo/OrderBookingComposite/USPSShipment/"
                xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
                xmlns:ora="http://schemas.oracle.com/xpath/extension"
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
                xmlns:imp1="http://www.globalcompany.example.com/ns/legacyOrder"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:ldap="http://schemas.oracle.com/xpath/extension/ldap"
                exclude-result-prefixes="xsi xsl wsdl ns3 tns ns1 ns2 ns4 inp1 ns0 xsd plt ns5 imp1 bpws xpath20 ora oraext dvm hwf mhdr med ids xdk xref ldap">
  <xsl:template match="/">
    <imp1:PurchaseOrder>
      <imp1:CustID>
        <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:CustomerId"/>
      </imp1:CustID>
      <imp1:ID>
        <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderId"/>
      </imp1:ID>
      <imp1:ShipTo>
        <imp1:Name>
          <imp1:Last>
            <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:ShipToName"/>
          </imp1:Last>
        </imp1:Name>
        <imp1:Address>
          <imp1:Street>
            <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:Address1"/>
          </imp1:Street>
          <imp1:City>
            <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:City"/>
          </imp1:City>
          <imp1:State>
            <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:StateProvince"/>
          </imp1:State>
          <imp1:Zip>
            <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:PostalCode"/>
          </imp1:Zip>
          <imp1:Country>
            <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:CountryId"/>
          </imp1:Country>
        </imp1:Address>
      </imp1:ShipTo>
      <imp1:UserContact>
        <imp1:PhoneNumber>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:ShipToPhoneNumber"/>
        </imp1:PhoneNumber>
      </imp1:UserContact>
      <imp1:OrderItems>
        <xsl:for-each select="/inp1:orderInfoVOSDO/inp1:OrderItemsInfoVO">
          <imp1:Item>
            <imp1:ProductName>
              <xsl:value-of select="inp1:ProductName"/>
            </imp1:ProductName>
            <imp1:price>
              <xsl:value-of select="inp1:UnitPrice"/>
            </imp1:price>
            <imp1:Quantity>
              <xsl:value-of select="inp1:Quantity"/>
            </imp1:Quantity>
          </imp1:Item>
        </xsl:for-each>
      </imp1:OrderItems>
      <imp1:OrderInfo>
        <xsl:choose>
          <xsl:when test="/inp1:orderInfoVOSDO/inp1:OrderDate/node()">
            <imp1:OrderDate>
              <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderDate"/>
            </imp1:OrderDate>
          </xsl:when>
          <xsl:otherwise>
            <imp1:OrderDate>
              <xsl:value-of select="xpath20:current-dateTime()"/>
            </imp1:OrderDate>
          </xsl:otherwise>
        </xsl:choose>
        <imp1:OrderPrice>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderTotal"/>
        </imp1:OrderPrice>
        <imp1:OrderStatus>
          <xsl:value-of select="/inp1:orderInfoVOSDO/inp1:OrderStatusCode"/>
        </imp1:OrderStatus>
      </imp1:OrderInfo>
    </imp1:PurchaseOrder>
  </xsl:template>
</xsl:stylesheet>
