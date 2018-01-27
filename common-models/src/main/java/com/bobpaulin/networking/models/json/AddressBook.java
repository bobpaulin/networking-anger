package com.bobpaulin.networking.models.json;

import java.util.ArrayList;
import java.util.List;

public class AddressBook {
	private List<Person> addressList = new ArrayList<Person>();

	public List<Person> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<Person> addressList) {
		this.addressList = addressList;
	}
}
