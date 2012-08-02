package org.atemsource.atem.utility.transform.api;

import java.util.Map;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePath;

public interface AttributeTransformation<A, B> {

	void mergeBA(B b, A a, TransformationContext ctx);

	void mergeAB(A a, B b, TransformationContext ctx);

	AttributePath getAttributeA();

	AttributePath getAttributeB();

	Converter getConverter();

	Map<String, Object> getMeta();

	EntityType getTypeA();

	EntityType getTypeB();

}