package org.group13.routingLogic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.group13.AggregationStrategies.InvoiceEnrichment;
import org.group13.dataObjects.Invoice;
import org.group13.dataObjects.Order;
import org.group13.transformerBeans.ConvertToInvoiceBean;
import org.group13.transformerBeans.ConvertToOrderBean;

public class InvoiceManagement extends RouteBuilder{
	
	// Represents the Invoice Management swimlane
	
	public void configure() {
		
		// Invoice processor
		// we extract from ProcessedInvoices queue
		// which contains Invoice POJOs
		
		// we want to enrich them from local DB
		// and then check whether invoice exists or not, and act accordingly
		
		 from("jms:ProcessedInvoices")
		 .process(new Processor() {
        	public void process(Exchange exchange) {
        		Invoice invoice= exchange.getIn().getBody(Invoice.class);
        		final InvoiceEnrichment invoiceEnricher = new InvoiceEnrichment();
        		invoiceEnricher.enrich(invoice);
        		exchange.getIn().setBody(invoice);
        	}
        })
        .to("jms:EnrichedInvoices")
        .bean(InvoiceRouter.class)
        .recipientList(header("recipients"));
		 
		 from("jms:Accounting")
	        .process(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	           	 	Invoice invoice= exchange.getIn().getBody(Invoice.class);
	           	 	exchange.getIn().setBody(mailMessageBody(true,invoice));
	           	 	exchange.getIn().setHeader("Subject", "IT Master: Pending invoice");
	            }
	        })
	        .to("smtps://smtp.gmail.com:465?username=workflowtestaccout@gmail.com&password=workflowtestaccount1950&debugMode=false", 
	        		"jms:invoiceApproved");
		 
		 
		 from("jms:InvalidInvoices")
	        .process(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	           	 	Invoice invoice= exchange.getIn().getBody(Invoice.class);
	           	 	exchange.getIn().setBody(mailMessageBody(false,invoice));
	           	 	exchange.getIn().setHeader("Subject", "IT Master: Invalid invoice");
	            }
	        })
	        .to("smtps://smtp.gmail.com:465?username=workflowtestaccout@gmail.com&password=workflowtestaccount1950&debugMode=false", 
	        		"jms:invoiceRejected");  
		 
	}
	public String mailMessageBody(boolean acceptance, Invoice invoice){
		String message;
		if(acceptance)
			message = "Dear Accounting team \n\n";
		else
			message = "Dear "+invoice.getCustomer()+", "+ invoice.getContact() +"\n\n";
		message += "The following order has been ";
		if(acceptance)		
			message += "invoiced:\n\n";
		else
			message += "rejected:\n\n";	
		message += "Item: "+invoice.getItemDescription()+"\n";
		message += "Quantity: "+invoice.getItemQuantity()+"\n";
		message += "Order Code: "+invoice.getOrderCode()+"\n\n";
		if(acceptance)		
			message += "Please, transfer funds to our supplier: "+invoice.getCustomer()+"\n";
		else
			message += "Because the order could not be traced back in our system\n";
		message += "\n";
		message += "Best regards,\n";
		message += "IT-master";
		return message;
	}
}
