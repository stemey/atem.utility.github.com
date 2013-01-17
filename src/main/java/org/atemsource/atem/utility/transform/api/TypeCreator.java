package org.atemsource.atem.utility.transform.api;

import javax.inject.Inject;
import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* Simple transformation to use as typeConverter for EntityTypeTransformations. In the case of merging it will compare the target type of this transformation to the type of the given instance. If they differs an instance of the desired target will be created and the attributes from the given instance will be copied to the new instance. 
*/
@Component
@Scope("prototype")
public class TypeCreator<A, B> implements Transformation<A, B>
{

	private UniTypeCreator<A, B> ab;

	private UniTypeCreator<B, A> ba;

	@Inject
	private BeanLocator beanLocator;

	EntityType sourceType;

	EntityType targetType;

	@Override
	public UniTransformation<A, B> getAB()
	{
		return ab;
	}

	@Override
	public UniTransformation<B, A> getBA()
	{
		return ba;
	}

	@Override
	public Type getTypeA()
	{
		return sourceType;
	}

	@Override
	public Type getTypeB()
	{
		return targetType;
	}

	void initialize(EntityType sourceType, EntityType targetType)
	{
		this.sourceType = sourceType;
		this.targetType = targetType;
		ab = beanLocator.getInstance(UniTypeCreator.class);
		ab.initialize(sourceType, targetType);
		ba = beanLocator.getInstance(UniTypeCreator.class);
		ba.initialize(targetType, sourceType);
	}

}
