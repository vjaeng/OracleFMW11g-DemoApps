package com.otn.sample.fod.soa.internalsupplier;

// external interfaces of the supplier ejb
import com.otn.sample.fod.soa.externalps.IExternalPartnerSupplierService;
import com.otn.sample.fod.soa.externalps.OrderItem;
import com.otn.sample.fod.soa.externalps.PriceQuote;
import com.otn.sample.fod.soa.externalps.exception.ExternalSupplierException;
// internal 
import com.otn.sample.fod.soa.internalsupplier.exception.InternalSupplierException;

import java.util.List;
import java.util.Vector;
// logger
import java.util.logging.Level;
// the interface for the outbound quote writer
import writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.QuoteRequest;
import writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.Write_ptt;
// globally available logger bean
import oracle.soa.platform.component.spring.beans.ILoggerBean;
// globally available header helper bean
import oracle.soa.platform.component.spring.beans.IHeaderHelperBean;

/**
 * Mediator implementation to convert between the internal format and the
 * legacy format from the External Partner Supplier. This is a classical example
 * where java comes in super handy, as a Vector (= the legacy format) can't be
 * easily converted into XML and back, w/o attd'l metadata
 * @author clemens utschig (clemens.utschig@oracle.com)
 */
public class InternalSupplierMediator implements IInternalPartnerSupplier 
{
    /**
     * Constant for the filename header property for the file adapter
     */
    private final static String STR_FILENAME_PROP_KEY = "jca.file.FileName";
    
    /**
     * Reference to the external partner supplier. This property is configured
     * in the spring ctx as <sca:reference/> and wired to an EJB 3.0 in the 
     * composite.xml 
     */
    private IExternalPartnerSupplierService externalPartnerSupplier;
    /**
     * The reference to the outside quote writer. This property is configured
     * in the spring ctx as <sca:reference/> and wired to a fileadapter in the 
     * composite.xml 
     */
    private Write_ptt quoteWriter;
    /**
     * Logger, spring injected. Note that the logger is context aware. This 
     * means, logging output will go to the "odl-handler", which is goes into
     * $server_home/logs/'server name'-diagnostic.log. Secondly all messages 
     * are prefixed with [compositeInstanceId/componentInstanceId] and lastly
     * the level can be changed via EM, oracle.integration.platform.blocks.java.
     * beans.impl.LoggerBeanImpl
     */
    private ILoggerBean logger;
    /**
     * HeaderHelper bean, spring injected. Used to get and set headers.
     */
    private IHeaderHelperBean headerHelper;
    
    /**
     * Get the price for a list of orderItems and write the quote via 
     * injected reference
     * @param pOrderItems the list of orderItems
     * @return the price for the list of orderItems
     */
    @Override
    public double getPriceForOrderItemList(List<Orderitem> pOrderItems) 
        throws InternalSupplierException
    {
        logger.log(Level.FINER,
          "--> ExternalSupplier: " + externalPartnerSupplier + 
          " Write_ptt: " + quoteWriter);
        // this is the externally injectect reference based on the spring ctx
        if (externalPartnerSupplier == null)
            throw new InternalSupplierException
                ("ExternalPartnerSupplier not available, check spring context");
        if (quoteWriter == null)
            throw new InternalSupplierException
                ("Quotewriter not available, check spring context");
        if (headerHelper == null)
            throw new InternalSupplierException
                ("HeaderHelper not available, check spring context");
                
        // convert the internal structure to the legacy one
        Vector legacyItems = copyInternalOrderItemsToLegacyFormat(pOrderItems);
        try 
        {
            // get the price quote from the external supplier
            PriceQuote quote = 
                externalPartnerSupplier.getQuoteForItems(legacyItems);            
            // write the quote out, after setting a filename dynamically
            String fileName = new StringBuffer().
                append("Order_").append(legacyItems.size()).append("_date_").
                append(new java.util.Date().getTime()).append(".txt").
                    toString();
            logger.log(Level.FINE, "Setting filename for orderlist: " +
                       fileName);
            headerHelper.setHeaderProperty(
                STR_FILENAME_PROP_KEY, fileName);
            // use the injected file adapter reference
            quoteWriter.write(
                copyInternalOrderItemsToQuoteRequest(pOrderItems));
            logger.log(Level.FINE, "Successfully wrote quote ..");
                
            return quote.getPrice();
        } catch (ExternalSupplierException externalEx) 
        {
            StringBuffer errMsg = new StringBuffer().
                append ("An ExternalSupplierException occured in the " ).
                append ("mediator");
            throw convertToInternalSupplierEx(errMsg, externalEx);
        } catch (Throwable allOthers) 
        {
            StringBuffer errMsg = new StringBuffer().
                append ("A generic exception occured in the mediator" );
            throw convertToInternalSupplierEx(errMsg, allOthers);
        }
    }
    
    /**
     * Internal helper to create a vector of legacy items 
     * @param pOrderItems the list of orderItems
     * @return a vector with legacy orderitems
     */
    private Vector copyInternalOrderItemsToLegacyFormat 
        (List<Orderitem> pOrderItems) 
    {
        logger.log(Level.FINER,
            "copyInternalOrderItemsToLegacyFormat: " + 
            (pOrderItems != null ? pOrderItems.size() : "empty"));
        if (pOrderItems == null || pOrderItems.size() == 0)
            return new Vector();
        Vector vLOrderitems = new Vector(pOrderItems.size());
        for (Orderitem internalOrderItem : pOrderItems) 
        {
            logger.log(Level.FINER,
              "-> productId: " + internalOrderItem.getProductId() + 
              " quantity: " + internalOrderItem.getQuantity() + 
              " price: " + internalOrderItem.getPrice());
            // create a legacy item
            OrderItem legacyItem = new OrderItem();
                // copy the values over from the internal one
                legacyItem.setPrice(internalOrderItem.getPrice());
                legacyItem.setQuantity(internalOrderItem.getQuantity());
                legacyItem.setProductId(internalOrderItem.getProductId());
            // add the new item to the vector
            vLOrderitems.add(legacyItem);
        }
        return vLOrderitems;
    }

    /**
     * Internal helper to create a quoteRequest from the interal order items
     * @param pOrderItems the list of orderItems
     * @return a QuoteRequest
     */
    private QuoteRequest copyInternalOrderItemsToQuoteRequest 
        (List<Orderitem> pOrderItems) 
    {
        QuoteRequest qRequest = new QuoteRequest();
        if (pOrderItems == null || pOrderItems.size() == 0)
            return qRequest;
        for (Orderitem internalOrderItem : pOrderItems) 
        {
            // create a new QRequest Product
            QuoteRequest.Product product = new QuoteRequest.Product();
                // copy the values over from the internal one
                product.setProductId(internalOrderItem.getProductId());
                product.setQuantity(internalOrderItem.getQuantity());
                product.setPrice(internalOrderItem.getPrice());
            // add the new product to the request
            qRequest.getProduct().add(product);
        }
        return qRequest;
    }

    /*******************************************************************
     *                 Spring injected members                         *
     *******************************************************************/

    public final void setExternalPartnerSupplier
        (IExternalPartnerSupplierService externalPartnerSupplier) 
    {
        this.externalPartnerSupplier = externalPartnerSupplier;
    }
    
    /**
     * Get the injected ExternalPartnerSupplier
     * @return the injected impl of the IExternalPartnerSupplier Service
     */
    public final IExternalPartnerSupplierService getExternalPartnerSupplier () 
    {
        return this.externalPartnerSupplier;
    }

    public void setQuoteWriter(Write_ptt quoteWriter) {
        this.quoteWriter = quoteWriter;
    }

    /**
     * Get the logger injected
     * @param logger an implementation of the ILoggerBean interface
     */
    public final void setLogger(ILoggerBean logger) {
        this.logger = logger;
    }
  
    public final ILoggerBean getLogger() {
        return logger;
    }

    /**
     * Get the injected QuoteWriter
     * @return the injected impl of the QuoteWriter Service
     */
    public Write_ptt getQuoteWriter() {
        return quoteWriter;
    }

    /**
     * Injected headerHelper
     * @param headerHelper
     */
    public final void setHeaderHelper(IHeaderHelperBean headerHelper) {
        this.headerHelper = headerHelper;
    }
  
    public final IHeaderHelperBean getHeaderHelper() {
        return headerHelper;
    }

    
    /********************************************************************
     *                       Helper methods                             *
     ********************************************************************/
    
    private InternalSupplierException convertToInternalSupplierEx 
        (StringBuffer pMessage, Throwable pBaseEx) 
    {
        pMessage.append(" [").
            append( pBaseEx.getMessage() != null ? 
                pBaseEx.getMessage() : "<unknown>" ).
            append("]\n Please refer to the cause.");
        InternalSupplierException iSE =
            new InternalSupplierException (pMessage.toString());
        iSE.initCause(pBaseEx);
        logger.log(Level.WARNING, "convertToInternalSupplierEx", iSE);
        return iSE;        
    }

}
