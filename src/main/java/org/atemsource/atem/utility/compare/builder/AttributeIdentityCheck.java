package org.atemsource.atem.utility.compare.builder;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;

public class AttributeIdentityCheck implements IdentityCheck {

	private SingleAttribute<?> idAttribute;

	@Override
	public <E> boolean isIdentical(E e1, E e2) {
		return idAttribute.isEqual(e1, e2);
	}

	public AttributeIdentityCheck(SingleAttribute<?> idAttribute) {
		super();
		this.idAttribute = idAttribute;
	}

}
