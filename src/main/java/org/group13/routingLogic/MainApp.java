package org.group13.routingLogic;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.main.Main;
import org.apache.commons.dbcp.BasicDataSource;
//import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
    	 //CamelContext context = new DefaultCamelContext();
         
         // connect to embedded ActiveMQ JMS broker
 		SimpleRegistry registry = new SimpleRegistry();

 		// code to create data source here
 		String url = "org.postgresql.Driver@localhost:5432:Procurement";
 		final DataSource ds = setupDataSource(url);
 		registry.put("ProcurementDB", ds);

 		CamelContext context = new DefaultCamelContext(registry);
 		initializeDatabase(ds);
 		
 		 ConnectionFactory connectionFactory =  new ActiveMQConnectionFactory("vm://localhost");
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
    private static DataSource setupDataSource(String connectURI) {
    	 BasicDataSource ds = new BasicDataSource();
         ds.setDriverClassName("org.postgresql.Driver");
         ds.setUrl("jdbc:postgresql://localhost:5432/Procurement");
         ds.setUsername("postgres");
         ds.setPassword("postgres");
         return ds;
    }
    
    private static void initializeDatabase(DataSource ds){
    	JdbcTemplate jdbc = new JdbcTemplate(ds);
 		jdbc.execute("CREATE TABLE IF NOT EXISTS Orders_log "+
				"("+
				"ID SERIAL PRIMARY KEY, "+
				"Time_inserted timestamp default current_timestamp, "+
				"Company varchar(255) NOT NULL, "+
				"Description varchar(255), "+
				"Quantity varchar(255)"+
			")");
 		jdbc.execute("CREATE TABLE IF NOT EXISTS Invoices_log "+
				"("+
				"ID SERIAL PRIMARY KEY, "+
				"Time_inserted timestamp default current_timestamp, "+
				"Company varchar(255) NOT NULL, "+
				"Description varchar(255), "+
				"Quantity varchar(255)"+
			")");
    }
}