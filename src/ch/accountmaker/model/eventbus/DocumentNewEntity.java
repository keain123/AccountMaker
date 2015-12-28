package ch.accountmaker.model.eventbus;

import ch.accountmaker.model.Customer;

public class DocumentNewEntity {
	
	private int docId;
	
	private int year;
	
	private int month;
	
	private Customer customer;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DocumentNewEntity(int year, int month) {
		this.year = year;
		this.month = month;
	}
	
	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}
	
}
