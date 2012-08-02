package org.atemsource.atem.utility.transform.impl.transformation;

import org.atemsource.atem.utility.transform.api.TransformationContext;

public interface UniAttributeTransformation<A, B> {
	public void merge(A a, B b, TransformationContext ctx);
}
