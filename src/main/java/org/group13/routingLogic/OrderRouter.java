package org.group13.routingLogic;

import java.util.Arrays;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.group13.dataObjects.Order;

public class OrderRouter {

	public void determine(Order order,@Headers Map<String,Object> headers){
		if(order.isAvailable())
			headers.put("recipients",Arrays.asList("jms:Shipping"));
		else
			headers.put("recipients",Arrays.asList("jms:Unavailable"));
	}
}
