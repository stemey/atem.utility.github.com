package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.attribute.Attribute;

public interface AttributeNameConverter {

	String convert(Attribute<?, ?> attribute);

}
