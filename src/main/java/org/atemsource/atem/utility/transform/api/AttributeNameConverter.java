package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.attribute.Attribute;

/**
* This Converter defines a standard strategy to create target attribute names for source attributes. Used by Binding. TODO add to TypeTransformationBuilderFactory. 
*/
public interface AttributeNameConverter {

/**
* returns the target attribute name for the given source attribute.
*/
	String convert(Attribute<?, ?> attribute);

}
