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
		// and then put in jdbc:ProcurementDB queue
		// which is configured in MainApp as datasource in registry
		// so basically the object is stored in DB
		from("jms:InvoicesTap")
	    .process(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	           	Invoice invoice = exchange.getIn().getBody(Invoice.class);
	           	exchange.getIn().setBody("INSERT INTO Invoices_log (company,description,quantity) "
	           			+ "VALUES('" + invoice.getCustomer() + "','"
	           					+ invoice.getItemDescription() +"','"
	           					+ invoice.getItemQuantity() + "');");
	            
	            }
	            })
	        .to("jdbc:ProcurementDB");
		
		from("jms:OrdersTap")
	    .process(new Processor() {
	            public void process(Exchange exchange) throws Exception {
	           	Order order = exchange.getIn().getBody(Order.class);
	           	exchange.getIn().setBody("INSERT INTO Orders_log (company,description,quantity) "
	           			+ "VALUES('" + order.getCustomer() + "','"
	           					+ order.getItemDescription() +"','"
	           					+ order.getItemQuantity() + "');");
	            }
	        })
	     .to("jdbc:ProcurementDB");
	}
}