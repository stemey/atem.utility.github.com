package org.atemsource.atem.utility.transform.api;

public interface JavaTransformation<A, B>{
 void mergeAB(A a, B b);
 void mergeBA(B b, A a);
}
