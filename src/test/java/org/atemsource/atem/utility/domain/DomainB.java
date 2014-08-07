package org.atemsource.atem.utility.domain;

import java.util.List;

import org.atemsource.atem.api.attribute.annotation.Association;

public class DomainB {
	private boolean bField;

	private DomainA reverse;
	
	@Association(targetType=DomainA.class)
	private List<DomainA> domains;

	public List<DomainA> getDomains() {
		return domains;
	}

	public void setDomains(List<DomainA> domains) {
		this.domains = domains;
	}

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
