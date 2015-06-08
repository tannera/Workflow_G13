package org.group13.transformerBeans;



import java.util.List;

import org.group13.dataObjects.*;

public class ConvertToOrderBean {
	/*public String convert(Order order) {
        return order.getOrder();
    }*/
	public Order convert(List<String> orderCsv) {
		Order order= new Order();
		int counter=0;
		for(String cell : orderCsv) {
			//for(String cell : row) {
			if(counter==0)
				order.setCustomer(cell);
			if(counter==1)
				order.setItemDescription(cell);
			if (counter==2)
				order.setItemQuantity(Integer.parseInt(cell));
			if (counter==4)
				order.setContact(cell);
			counter++;
			//}
		}
		return order;
	}
}
