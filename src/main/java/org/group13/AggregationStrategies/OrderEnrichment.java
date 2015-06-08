package org.group13.AggregationStrategies;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.group13.dataObjects.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public final class OrderEnrichment {
	
    public Order enrich(Order order) {
        String url = "org.postgresql.Driver@localhost:5432:Procurement";
 		final DataSource ds = setupDataSource(url);

        String query = "Select * from stock where description='"+order.getItemDescription().trim()+"'";
        int availableQuantity = getAvailableQuantity(ds, query);
        /*System.out.println("---------------------------------------");
        System.out.println("Item: "+order.getItemDescription());
        System.out.println("Qty Requested: "+order.getItemQuantity());
        System.out.println("Qty Available: "+availableQuantity);
        System.out.println("---------------------------------------");
        */
        if(order.getItemQuantity()<=availableQuantity){
        	order.setAvailable(true);
			String updateQuery = "UPDATE Stock "
					+ " SET stockquantity = stockquantity - "+order.getItemQuantity()+""
					+ " WHERE description = '" + order.getItemDescription().trim()+"'";
			executeStatement(ds,updateQuery);
        }
        order.setItemCode(getItemCode(ds, query));
        
        return order;
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
    private int getAvailableQuantity(DataSource ds,String sql){
    	int availableQuantity=0;
    	JdbcTemplate jdbc = new JdbcTemplate(ds);
    	SqlRowSet srs = jdbc.queryForRowSet(sql);
    	while (srs.next()) {
			availableQuantity = Integer.parseInt(srs.getString("stockquantity"));
		}
    	return availableQuantity;
    }
    private String getItemCode(DataSource ds,String sql){
    	String itemcode="";
    	JdbcTemplate jdbc = new JdbcTemplate(ds);
    	SqlRowSet srs = jdbc.queryForRowSet(sql);
    	while (srs.next()) {
			itemcode = srs.getString("itemcode");
		}
    	return itemcode;
    }
}