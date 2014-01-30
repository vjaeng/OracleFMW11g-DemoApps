package com.otn.sample.fod.soa.externalps;

import com.otn.sample.fod.soa.externalps.exception.ExternalSupplierException;

import java.util.Iterator;
import java.util.Vector;

import java.util.logging.Logger;

import javax.ejb.Remote;
import javax.ejb.Stateless;

@Stateless(name = "SessionEJB", mappedName = "WebLogicFusionOrderDemo-ExternalLegacyPartnerSupplierEjb-SessionEJB")
@Remote
public class ExternalPartnerSupplierService 
    implements IExternalPartnerSupplierService 
{
    /**
     * The logger for this class
     */
    private Logger mLogger = 
        Logger.getLogger(ExternalPartnerSupplierService.class.getName());
    
    /**
     * Default constructor
     */
    public ExternalPartnerSupplierService() 
    {
        mLogger.info("New instance of " + 
                     ExternalPartnerSupplierService.class.getName());
    }
    
    /**
     * Get the quote for a set of order lines
     * @param pOrderLines the order lines via untyped vector, the perfect case
     * to use java for mediation
     * @return a somewhat "complex" PriceQuote object
     * @throws ExternalSupplierException in case an unknown object has been 
     *  passed
     */
    @Override
    public PriceQuote getQuoteForItems (Vector pOrderLines) 
        throws ExternalSupplierException
    {
        mLogger.info("<getQuoteForItems> Orderlines: " +
                     (pOrderLines == null ? "empty" : pOrderLines.size()));
        double price = 100.0;
        if (pOrderLines != null) 
        {
            price = 0.0;
            // old fashioned 1.4- way to work an iterator. This is just to
            // show the pain of untyped collections.
            Iterator orderLineIt = pOrderLines.iterator();
            while (orderLineIt.hasNext()) 
            {
                Object o = orderLineIt.next();
                if (o instanceof OrderItem) 
                {
                    OrderItem item = (OrderItem)o;
                    mLogger.info("adding item: " + item.getProductId() + 
                        " Price: " + item.getPrice() + " quantity: " + 
                        item.getQuantity());
                    price = price + (item.getPrice() * item.getQuantity());
                    mLogger.info("calculated orderprice: " + price);
                }
                else 
                {
                    // if someone sticks unwanted items into the vector -
                    // something we can't check upfront
                    throw new ExternalSupplierException ("Cannot use item, " + 
                        "is of type: " + o.getClass().getName() + ", must be"
                        + " of type: " + OrderItem.class.getName());    
                }
            }
        }
        mLogger.info("final price: " + price);
        return new PriceQuote(price);    
    }
}
