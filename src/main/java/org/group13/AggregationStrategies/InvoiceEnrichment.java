package org.group13.AggregationStrategies;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.group13.dataObjects.Invoice;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class InvoiceEnrichment {
	 public Invoice enrich(Invoice invoice) {
	        String url = "org.postgresql.Driver@localhost:5432:Procurement";
	 		final DataSource ds = setupDataSource(url);
	 		String query = "SELECT * FROM orders WHERE ordercode='"+invoice.getOrderCode().trim()+"'";
	        if(validateInvoice(ds,query,invoice)){
	        	// if the invoice is valid, we can delete the entry from the orders table
	        	invoice.setAvailable(true);
				String updateQuery = "DELETE FROM Orders "
						+ " WHERE ordercode = '" + invoice.getOrderCode().trim()+"'";
				executeStatement(ds,updateQuery);
	        }
	        //invoice.setItemCode(getItemCode(ds, query));
	        return invoice;
	    }
	    private static DataSource setupDataSource(String connectURI) {
	   	 BasicDataSource ds = new BasicDataSource();
	        ds.setDriverClassName("org.postgresql.Driver");
	        ds.setUrl("jdbc:postgresql://localhost:5432/Procurement");
	        ds.setUsername("postgres");
	        ds.setPassword("postgres");
	        return ds;
	   }
	    private static void executeStatement(DataSource ds,String statement){
	    	JdbcTemplate jdbc = new JdbcTemplate(ds);
	    	jdbc.execute(statement);
	    }
	    private boolean validateInvoice(DataSource ds,String sql,Invoice invoice){
	    	JdbcTemplate jdbc = new JdbcTemplate(ds);
	    	SqlRowSet srs = jdbc.queryForRowSet(sql);
	    	while (srs.next()) {
	    		// check if code, quantity and description from the invoice match what is in the order DB
	    		// if yes, return true!
				if(invoice.getOrderCode().trim().equalsIgnoreCase(srs.getString("ordercode"))
						&& invoice.getItemDescription().trim().equalsIgnoreCase(srs.getString("description"))
								&& invoice.getItemQuantity() == Integer.parseInt(srs.getString("quantity"))){
					return true;
				}
	    	}
	    	// otherwise, the invoice is not valid!
	    	return false;
	    }
}
