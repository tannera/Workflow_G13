package org.group13.routingLogic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.group13.dataObjects.Invoice;
import org.group13.dataObjects.Order;
import org.group13.transformerBeans.ConvertToInvoiceBean;
import org.group13.transformerBeans.ConvertToOrderBean;

public class DataLogging extends RouteBuilder{
	public void configure() {
		// used Wiretap, which created a logging copy
		// add some custom logic for storing
		
		/*from("jms:InvoicesTap")
	    .process(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	           	 Invoice invoice = exchange.getIn().getBody(Invoice.class);
	           	 System.out.println("Wiretaped invoice quantity "+invoice.getItemQuantity());
	            }
	        });
		
		from("jms:OrdersTap")
	    .process(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	           	 Order order = exchange.getIn().getBody(Order.class);
	           	 System.out.println("Wiretaped order quantity "+order.getItemQuantity());
	            }
	        });*/
	}
}
