package org.atemsource.atem.utility.path.parser;

import org.atemsource.atem.utility.path.AttributePathBuilder;

public class ParboiledAttributePathBuilder {
	private AttributePathBuilder builder;

	public boolean addElement(String match) {
		builder.addElement(match);
		return true;
	}

	public ParboiledAttributePathBuilder(AttributePathBuilder builder) {
		super();
		this.builder = builder;
	}

}
