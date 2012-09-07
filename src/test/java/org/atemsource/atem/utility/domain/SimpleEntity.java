package org.atemsource.atem.utility.domain;

import java.util.List;

import org.atemsource.atem.api.attribute.annotation.Association;

public class SimpleEntity {
	private String property;

	@Association(targetType = String.class)
	private List<String> texts;
	@Association(targetType = VerySimpleEntity.class)
	private List<VerySimpleEntity> veries;

	public List<VerySimpleEntity> getVeries() {
		return veries;
	}

	public void setVeries(List<VerySimpleEntity> veries) {
		this.veries = veries;
	}

	public List<String> getTexts() {
		return texts;
	}

	private VerySimpleEntity very;

	public VerySimpleEntity getVery() {
		return very;
	}

	public void setVery(VerySimpleEntity very) {
		this.very = very;
	}

	public void setTexts(List<String> texts) {
		this.texts = texts;
	}

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
