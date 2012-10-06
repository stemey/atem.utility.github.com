package org.atemsource.atem.utility.transform.api;

import javax.inject.Inject;


import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.impl.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class UniTypeCreator<A, B> implements UniTransformation<A, B> {

	private Type<B> targetType;
	private Type<A> sourceType;


	@Override
	public Type<A> getSourceType() {
		return this.sourceType;
	}
	
	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Override
	public Type<B> getTargetType() {
		return this.targetType;
	}

	@Override
	public B merge(A a, B b, TransformationContext ctx) {
		if (ctx.isTransformed(a)) {
			return (B) ctx.getTransformed(a);
		} else {
			EntityType<B> entityTypeB =entityTypeRepository.getEntityType(b);
			if (entityTypeB.equals(getTargetType())) {
				return b;
			}else{
				 B newB = ((EntityType<B>)getTargetType()).createEntity();
				 EntityType<B> commonAncestor =  EntityUtils.getCommonAncestor((EntityType<A>) getSourceType(), entityTypeB);
				 if (commonAncestor!=null) {
					 EntityUtils.merge(b, newB, commonAncestor);
				 }
				 return newB;
			}
		}
	}

	@Override
	public B convert(A a, TransformationContext ctx) {
		if (ctx.isTransformed(a)) {
			return (B) ctx.getTransformed(a);
		} else {
			return (B) ((EntityType<B>)getTargetType()).createEntity();
		}
	}

	public void initialize(Type<A> sourceType, Type<B> targetType) {
		this.targetType = targetType;
		this.sourceType = sourceType;
	}

	@Override
	public Type<? extends B> getTargetType(Type<? extends A> sourceType) {
		return targetType;
	}
}