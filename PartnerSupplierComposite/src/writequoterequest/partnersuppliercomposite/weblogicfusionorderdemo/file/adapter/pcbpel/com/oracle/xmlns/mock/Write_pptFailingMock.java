package writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.mock;

import writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.QuoteRequest;
import writequoterequest.partnersuppliercomposite.weblogicfusionorderdemo.file.adapter.pcbpel.com.oracle.xmlns.Write_ptt;

/**
 * Mock class that fails upon write request to test error handling.
 * @author clemens utschig
 */
public class Write_pptFailingMock implements Write_ptt 
{
    public void write(QuoteRequest body) {
        throw new RuntimeException("Intentionally failing, negative testcase");
    }
}
