package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
/**
* Transformations handles inheritance by defualt as if there is a 
*/
public interface TransformationFinder<A,B> {
public  UniTransformation<A,B> getAB(A a, TransformationContext ctx);
public UniTransformation<B,A> getBA(B b, TransformationContext ctx);
}
