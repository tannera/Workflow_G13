package org.group13.routingLogic;

import java.util.Arrays;
import java.util.Map;

import org.apache.camel.Headers;
import org.group13.dataObjects.Invoice;

public class InvoiceRouter {
	public void determine(Invoice invoice,@Headers Map<String,Object> headers){
		if(invoice.isAvailable())
			headers.put("recipients",Arrays.asList("jms:Accounting"));
		else
			headers.put("recipients",Arrays.asList("jms:InvalidInvoices"));
	}
}
