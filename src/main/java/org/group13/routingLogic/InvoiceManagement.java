package org.group13.routingLogic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
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
	            public void process(Exchange exchange) throws Exception {
	           	 Invoice invoice= exchange.getIn().getBody(Invoice.class);
	           	 System.out.println("Invoiced quantity "+invoice.getItemQuantity());
	            }
	        });
		 
	}
}
