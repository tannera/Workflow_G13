package org.group13.routingLogic;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.main.Main;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
    	 CamelContext context = new DefaultCamelContext();
         
         // connect to embedded ActiveMQ JMS broker
    	 ConnectionFactory connectionFactory = 
             new ActiveMQConnectionFactory("vm://localhost");
         context.addComponent("jms",
             JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
         context.addRoutes(new TransformationMapping());
         context.addRoutes(new OrderManagement());
         context.addRoutes(new InvoiceManagement());
         context.addRoutes(new DataLogging());
         context.start();
         Thread.sleep(10000);

         // stop the CamelContext
         context.stop();
    }
}

