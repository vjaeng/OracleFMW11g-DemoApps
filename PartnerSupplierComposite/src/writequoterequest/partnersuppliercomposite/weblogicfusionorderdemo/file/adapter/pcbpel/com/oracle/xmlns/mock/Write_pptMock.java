package writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.mock;

import writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.QuoteRequest;
import writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.Write_ptt;

/**
 * Mock test class for the adapter reference
 * @author clemens utschig
 */
public class Write_pptMock implements Write_ptt 
{
    
    /**
     * Public constructor
     */
    public Write_pptMock () 
    {
        System.out.println("Creating new instance of " + Write_pptMock.class);    
    }

    /**
     * Write a quoteRequest out
     * @param body the QuoteRequest object
     */
    @Override
    public void write(QuoteRequest body) 
    {
        System.out.println("Got write request for quote ..");
        for (QuoteRequest.Product product : body.getProduct()) 
        {
            System.out.println(" -> " +  product.getProductId() + " qty: " + 
                               product.getQuantity() + " price: " + 
                               product.getPrice());    
        }
    }
}
