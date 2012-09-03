package org.atemsource.atem.utility.domain;

import java.util.List;

import org.atemsource.atem.api.attribute.annotation.Association;

public class SimpleEntity {
	private String property;


	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	private int count;
}
