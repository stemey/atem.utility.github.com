package org.atemsource.atem.utility.transform.api;

import java.util.Map;
import java.util.Set;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePath;
/**
* A transformation of n attributes of a source type to m attributes of a target type.
*/
public interface AttributeTransformation<A, B> {

	void mergeBA(B b, A a, TransformationContext ctx);

	void mergeAB(A a, B b, TransformationContext ctx);

	Set<AttributePath> getAttributeAs();

	Set<AttributePath> getAttributeBs();

	Map<String, Object> getMeta();

	EntityType getTypeA();

	EntityType getTypeB();

}
