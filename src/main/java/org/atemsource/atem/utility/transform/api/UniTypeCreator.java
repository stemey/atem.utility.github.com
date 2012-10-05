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
public class UniTypeCreator<A, B> implements UniTransformation<A, B>
{

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Type<A> sourceType;

	private Type<B> targetType;

	@Override
	public B convert(A a, TransformationContext ctx)
	{
		if (ctx.isTransformed(a))
		{
			return (B) ctx.getTransformed(a);
		}
		else
		{
			return ((EntityType<B>) getTargetType()).createEntity();
		}
	}

	@Override
	public Type<A> getSourceType()
	{
		return this.sourceType;
	}

	@Override
	public Type<B> getTargetType()
	{
		return this.targetType;
	}

	public void initialize(Type<A> sourceType, Type<B> targetType)
	{
		this.targetType = targetType;
		this.sourceType = sourceType;
	}

	@Override
	public B merge(A a, B b, TransformationContext ctx)
	{
		if (ctx.isTransformed(a))
		{
			return (B) ctx.getTransformed(a);
		}
		else
		{
			EntityType<B> entityTypeB = ctx.getEntityTypeByB(b);
			if (entityTypeB == null)
			{
				entityTypeB = (EntityType<B>) targetType;
			}
			if (entityTypeB.equals(getTargetType()))
			{
				return b;
			}
			else
			{
				B newB = ((EntityType<B>) getTargetType()).createEntity();
				EntityType<B> commonAncestor = EntityUtils.getCommonAncestor((EntityType<A>) getSourceType(), entityTypeB);
				if (commonAncestor != null)
				{
					EntityUtils.merge(b, newB, commonAncestor);
				}
				return newB;
			}
		}
	}
}