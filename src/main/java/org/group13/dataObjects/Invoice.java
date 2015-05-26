package org.group13.dataObjects;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice {
	private String invoiceID;
	
	@XmlElement(name = "customer")
	private String customer;
	
	@XmlElement(name = "description")
	private String itemDescription;
	
	@XmlElement(name = "quantity")
	private int itemQuantity;
	
	private String itemID;
	private float bill;
	
	public String getInvoiceID(){
		return invoiceID;
	}
	public void setOrderID(String id){
		invoiceID = id;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}
	public int getItemQuantity() {
		return itemQuantity;
	}
	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}
	public String getItemID() {
		return itemID;
	}
	public void setItemID(String itemID) {
		this.itemID = itemID;
	}
	public float getBill() {
		return bill;
	}
	public void setBill(float price) {
		this.bill = price;
	}
}
