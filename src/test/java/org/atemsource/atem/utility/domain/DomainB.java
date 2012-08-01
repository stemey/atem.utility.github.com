package org.atemsource.atem.utility.domain;

public class DomainB {
	private boolean bField;

	private DomainA reverse;

	public DomainA getReverse() {
		return reverse;
	}

	public boolean isBField() {
		return bField;
	}

	public void setBField(boolean bField) {
		this.bField = bField;
	}

	public void setReverse(DomainA reverse) {
		this.reverse = reverse;
	}
}
