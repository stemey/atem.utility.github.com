package org.atemsource.atem.utility.transform.api;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.EntityTypeUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TypeCreator<A, B> implements Transformation<A, B> {

	EntityType sourceType;

	EntityType targetType;
	
	@Inject 
	private EntityTypeRepository entityTypeRepository;
	@Inject
	private BeanLocator beanLocator;

	private UniTypeCreator<A, B> ab;

	private UniTypeCreator<B, A> ba;

	void initialize( EntityType sourceType, EntityType targetType) {
		this.sourceType = sourceType;
		this.targetType = targetType;
		  ab = beanLocator.getInstance(UniTypeCreator.class);
		 ab.initialize( sourceType,targetType);
		  ba = beanLocator.getInstance(UniTypeCreator.class);
		 ba.initialize( targetType,sourceType);
	}

	@Override
	public UniTransformation<A, B> getAB() {
		return ab;
	}

	@Override
	public UniTransformation<B, A> getBA() {
		return ba;
	}

	@Override
	public Type getTypeA() {
		return sourceType;
	}

	@Override
	public Type getTypeB() {
		return targetType;
	}

}