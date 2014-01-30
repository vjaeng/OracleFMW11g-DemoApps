package com.example.globalcompany.ccauth;

import com.example.globalcompany.ccauth.types.AuthInformation;

import com.example.globalcompany.ccauth.types.ObjectFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

@WebService(name = "CreditAuthorizationPort", targetNamespace = "http://www.globalcompany.example.com/ns/CreditAuthorizationService", serviceName = "CreditAuthorizationService", portName = "CreditAuthorizationPort", wsdlLocation="WEB-INF/wsdl/CreditAuthorizationService.wsdl")
@XmlSeeAlso( { ObjectFactory.class })
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class CreditAuthorizationPortImpl {
    public CreditAuthorizationPortImpl() {
    }

    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    @Action(input = "http://www.globalcompany.example.com/ns/CreditAuthorizationService/AuthorizeCredit", output = "http://www.globalcompany.example.com/ns/CreditAuthorizationService/CreditAuthorizationPort/AuthorizeCreditResponse", fault = { @FaultAction(className =
                        CreditAuthorizationFaultMessage.class, value = "http://www.globalcompany.example.com/ns/CreditAuthorizationService/CreditAuthorizationPort/AuthorizeCredit/Fault/InvalidCredit")
                } )
    @WebMethod(operationName = "AuthorizeCredit", action = "http://www.globalcompany.example.com/ns/CreditAuthorizationService/AuthorizeCredit")
    @WebResult(name = "status", targetNamespace = "http://www.globalcompany.example.com/ns/CCAuthorizationService", partName = "status")
    public String authorizeCredit(@WebParam(name = "AuthInformation", partName = "Authorization", targetNamespace = "http://www.globalcompany.example.com/ns/CCAuthorizationService")
        AuthInformation Authorization) throws CreditAuthorizationFaultMessage {
        String status = "APPROVED";
        String ccTypes[] = { "AMEX", "VISA", "MSTR", "DINC"};
        boolean validCCType = false;
        
                                                                    
        for(String type: ccTypes)     {
            if (type.equals(Authorization.getCCType())){
                
               validCCType = true;
               break;
            }
         }
        
        if (!validCCType) {
            throw new CreditAuthorizationFaultMessage("Invalid Credit Card Type","Invalid Credit Card Type");     
        }
        
        else {
            char ccStartsWith = Authorization.getCCNumber().charAt(0);
            switch(ccStartsWith) {
            case '0' : status = "INVALID"; break;
            case '1' : status = "DENIED"; break;
            case '2' : status = "STOLEN"; break;
            case '3' : status = "FRAUD_ALERT"; break;
                
           }
        }   
           
        
        return status;

    }
}
