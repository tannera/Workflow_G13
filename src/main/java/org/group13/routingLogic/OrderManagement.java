package org.group13.routingLogic;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.group13.dataObjects.Invoice;
import org.group13.dataObjects.Order;
import org.group13.AggregationStrategies.*;

public class OrderManagement extends RouteBuilder{
	
	// Represents the Order Management swimlane
	
	public void configure() {
		
		// get the order objects from the processed queue
		// and enrich them using a class in processor
		//with availability and code information

        from("jms:ProcessedOrders")
        .process(new Processor() {
        	public void process(Exchange exchange) {
        		Order result= exchange.getIn().getBody(Order.class);
        		final OrderEnrichment orderEnricher = new OrderEnrichment();
        		orderEnricher.enrich(result);
        		exchange.getIn().setBody(result);
        	}
        })
        .to("jms:EnrichedOrders")
        .bean(OrderRouter.class)
        .recipientList(header("recipients"));
        
        from("jms:Shipping")
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
           	 	Order order= exchange.getIn().getBody(Order.class);
           	 	exchange.getIn().setBody(mailMessageBody(true,order));
           	 	exchange.getIn().setHeader("Subject", "IT Master: Your Order will be shipped");
            }
        })
        .to("smtps://smtp.gmail.com:465?username=workflowtestaccout@gmail.com&password=workflowtestaccount1950&debugMode=false", 
        		"jms:orderShipped");
        
        
        from("jms:Unavailable")
        .process(new Processor() {
            public void process(Exchange exchange) throws Exception {
            	Order order= exchange.getIn().getBody(Order.class);
            	exchange.getIn().setBody(mailMessageBody(false,order));
            	exchange.getIn().setHeader("Subject", "IT Master: Your Order Has been Rejected");
            }
        })
        .to("smtps://smtp.gmail.com:465?username=workflowtestaccout@gmail.com&password=workflowtestaccount1950&debugMode=false", 
        		"jms:orderRejected");
        
        }
	public String mailMessageBody(boolean acceptance, Order order){
		String message;
		message = "Dear "+order.getCustomer()+", "+ order.getContact() +"\n\n";
		message += "The following order has been ";
		if(acceptance)		
			message += "accepted:\n\n";
		else
			message += "rejected:\n\n";	
		message += "Item: "+order.getItemDescription()+"\n";
		message += "Quantity: "+order.getItemQuantity()+"\n";
		if(acceptance)		
			message += "And will be delivered to you in 5 days\n";
		else
			message += "Because the needed quantity is not currently on stock\n";
		message += "\n";
		message += "Best regards,\n";
		message += "IT-master";
		return message;
	}
}
