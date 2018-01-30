package com.bobpaulin.networking.models.json;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddressBook implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6962414706942554616L;
	private List<Person> addressList = new ArrayList<Person>();

	public List<Person> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<Person> addressList) {
		this.addressList = addressList;
	}
}
