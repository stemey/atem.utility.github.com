package org.atemsource.atem.utility.domain;

public class SubdomainA extends DomainA{
	public String getSubField() {
		return subField;
	}

	public void setSubField(String subField) {
		this.subField = subField;
	}

	private String subField;
}
