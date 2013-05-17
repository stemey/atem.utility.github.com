package org.atemsource.atem.utility.transform.impl.transformation;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Transformation;

public interface OneToOneAttributeTransformation<A, B> extends AttributeTransformation<A, B> {

	public abstract Transformation getTransformation();
	
	public AttributePath getAttributeA();
	public AttributePath getAttributeB();

}