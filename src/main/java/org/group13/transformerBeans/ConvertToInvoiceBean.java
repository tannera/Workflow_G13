package org.group13.transformerBeans;



import java.util.List;

import org.group13.dataObjects.*;

public class ConvertToInvoiceBean {
	/*public String convert(Order order) {
        return order.getOrder();
    }*/
	public Invoice convert(List<String> invoiceCsv) {
		Invoice invoice= new Invoice();
		int counter=0;
		for(String cell : invoiceCsv) {
			//for(String cell : row) {
			if(counter==0)
				invoice.setCustomer(cell);
			if(counter==1)
				invoice.setItemDescription(	cell);
			if (counter==2)
				invoice.setItemQuantity(Integer.parseInt(cell));
			if (counter==4)
				invoice.setContact(cell);
			if (counter==5)
				invoice.setBill(Float.parseFloat(cell));
			if (counter==6)
				invoice.setOrderCode(cell);
			counter++;
			//}
		}
		return invoice;
	}
}
