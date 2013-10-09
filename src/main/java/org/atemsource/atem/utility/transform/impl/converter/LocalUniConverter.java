package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;


public class LocalUniConverter implements UniConverter<Object, Object>
{
	private final AbstractLocalConverter<Object, Object> converter;

	private final boolean direction;

	public LocalUniConverter(AbstractLocalConverter converter, boolean direction)
	{
		super();
		this.direction = direction;
		this.converter = converter;
	}

	@Override
	public Object convert(Object a, TransformationContext ctx)
	{
		if (direction)
		{
			return converter.convertAB(a, ctx);
		}
		else
		{
			return converter.convertBA(a, ctx);
		}
	}

	@Override
	public Type<Object> getSourceType()
	{
		if (direction)
		{
			return converter.getTypeA();
		}
		else
		{
			return converter.getTypeB();
		}
	}

	@Override
	public Type<Object> getTargetType()
	{
		if (direction)
		{
			return converter.getTypeB();
		}
		else
		{
			return converter.getTypeA();
		}
	}

	@Override
	public Type<? extends Object> getTargetType(Type<? extends Object> sourceType)
	{
		return getTargetType();
	}

}
