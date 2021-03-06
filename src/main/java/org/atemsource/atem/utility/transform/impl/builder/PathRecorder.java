package org.atemsource.atem.utility.transform.impl.builder;

import java.lang.reflect.Method;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.path.AttributePathBuilder;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class PathRecorder implements MethodInterceptor {

	private AbstractAttributeTransformationBuilder<?, ?,?> builder;

	public PathRecorder(
			AbstractAttributeTransformationBuilder<?, ?,?> builder) {
		this.builder=builder;
	}
	

	@Override
	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		// TODO reuse utility code or move to AttributePathBuilder or move to EntityType.getAtributeForMethod
		if (method.getName().startsWith("get")) {
			String name = method.getName().substring(3, 4).toLowerCase()+method.getName().substring(4);
			return interceptAttribute(method, name);
		}else if (method.getName().startsWith("is")) {
			String name = method.getName().substring(2,3).toLowerCase()+method.getName().substring(3);
			return interceptAttribute(method, name);
		}else{
			throw new IllegalStateException("cannot find attribute for method "+method.getName());
			
		}
	}


	protected Object interceptAttribute(Method method, String name) {
		Attribute<?,?> attribute=((EntityType)builder.getSourcePathBuilder().getTargetType()).getAttribute(name);
		if (attribute==null) {
			throw new IllegalStateException("cannot find attribute for method "+method.getName());
		}else{
			builder.getSourcePathBuilder().addAttribute(attribute);
			Enhancer enhancer = new Enhancer();
			return enhancer.create(attribute.getTargetType().getJavaType(),this);
		}
	}

}
