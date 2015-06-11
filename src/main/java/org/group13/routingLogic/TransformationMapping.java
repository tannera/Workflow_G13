package org.group13.routingLogic;

import javax.jms.ConnectionFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.spi.DataFormat;
import org.group13.dataObjects.Invoice;
import org.group13.dataObjects.Order;
import org.group13.transformerBeans.ConvertToInvoiceBean;
import org.group13.transformerBeans.ConvertToOrderBean;

/**
 * A Camel Java DSL Router
 */
public class TransformationMapping extends RouteBuilder {

	
    public void configure() {
             // load file orders from src/data into the JMS queue
    	     
    	     from("file:src/data?noop=true&delay=5000").to("jms:incomingItems");
             // content-based router
             // Separate XML from CSV Orders
             // and place them in separate channels
             from("jms:incomingItems") 
             .choice()
                 .when(header("CamelFileName").endsWith(".xml"))
                     .to("jms:xmlItems")  
                 .when(header("CamelFileName").endsWith(".csv"))
                     .to("jms:csvItems")
             	  .otherwise()
             	  	.to("jms:badFiles").
             end();
 
             
             // for each file type:
             // 	* translate to POJO
             // 	* split into line items
             // 	* identify whether order/invoice/bad/test
             // 	* and transform him into that object
             
             //Process CSV files now
             // get objects from csvlOrders channel, transform to POJO, translate
             from("jms:csvItems")
             // convert it to an appropriate POJO 
             .unmarshal().csv()
             //and split individual line items
             .split(body())
             //now we have to identify what kind of item it is
             // we identify test orders as line items
             // due to absence of xml-like headers
             .choice()
             	// place in appropriate queue
             	 .when(body().contains("Order"))
                     .to("jms:csvProcessedOrders")
                 .when(body().contains("Invoice"))
                     .to("jms:csvProcessedInvoices")
                 .when(body().contains("Test"))
                     .to("jms:csvProcessedTests")
                   // otherwise not an order or invoice
                   // so put me in a bad objects queue
             	  .otherwise()
             	  	.to("jms:badObjects")
             .end();
            
             from("jms:csvProcessedOrders").bean(ConvertToOrderBean.class)
             .to("jms:ProcessedOrders")
    		 .wireTap("jms:OrdersTap");
             
             from("jms:csvProcessedInvoices").bean(ConvertToInvoiceBean.class)
    		 .to("jms:ProcessedInvoices")
    		 .wireTap("jms:InvoicesTap");
             
             // now, process XML files
             // First, we have to separate test items
             // we look in the header and try to find appropriate keyword
             from("jms:xmlItems")
             	.choice()
             	.when(xpath("/test"))
                     .to("jms:xmlTestOrders")
                .otherwise().to("jms:xmlItemsApproved");
            
             // content based filter, identifying invoices from orders
             from("jms:xmlItemsApproved")
           	 	.choice()
           	 	.when(xpath("/invoices"))
                   .to("jms:xmlInvoices")
                .otherwise()
                .to("jms:xmlOrders");
             
             // generate neccessary JAXB objects
             JaxbDataFormat invoicesData = new JaxbDataFormat();
             JAXBContext conInvoices=null;
			 try {
				conInvoices = JAXBContext.newInstance(Invoice.class);
			 } catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
             invoicesData.setContext(conInvoices);
             
             JaxbDataFormat ordersData = new JaxbDataFormat();
             JAXBContext conOrders=null;
			 try {
				conOrders = JAXBContext.newInstance(Order.class);
			 } catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
             ordersData.setContext(conOrders);
             
            
             // get objects from invoices channel, and convert to POJO list         
             from("jms:xmlInvoices")
             //split into individual XML invoices
             .split(body().tokenizeXML("invoice","invoices")).streaming()
             .unmarshal(invoicesData)
             //.split(body())
             .to("jms:ProcessedInvoices")
             .wireTap("jms:InvoicesTap");
             
             // get objects from orders channel, and convert to POJO list
             from("jms:xmlOrders")   
             //split into individual XML orders
             .split(body()
            		 .tokenizeXML("order","orders")).streaming()
             .unmarshal(ordersData)
             .to("jms:ProcessedOrders")
             .wireTap("jms:OrdersTap");
         }
}
