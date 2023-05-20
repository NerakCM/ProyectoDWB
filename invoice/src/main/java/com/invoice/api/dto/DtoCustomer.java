package com.invoice.api.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

public class DtoCustomer {

	private String rfc;

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

}
