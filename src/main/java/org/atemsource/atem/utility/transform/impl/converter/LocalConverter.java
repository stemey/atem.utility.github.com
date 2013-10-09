package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;


public class LocalConverter<A, B> extends AbstractLocalConverter<A, B> implements Converter<A, B>, Constraining
{

	private final boolean constraining;

	private final JavaConverter javaConverter;

	public LocalConverter(JavaConverter javaConverter, Type<A> typeA, Type<B> typeB)
	{
		super();
		this.javaConverter = javaConverter;
		this.constraining = javaConverter instanceof Constraining;
		this.typeA = typeA;
		this.typeB = typeB;
	}

	public Object convertAB(Object a, TransformationContext ctx)
	{
		return javaConverter.convertAB(a, ctx);
	}

	public Object convertBA(Object a, TransformationContext ctx)
	{
		return javaConverter.convertBA(a, ctx);

	}

	@Override
	public Object getConstraintAB(String name)
	{
		if (constraining)
		{
			return ((Constraining) javaConverter).getConstraintAB(name);
		}
		else
		{
			return null;
		}
	}

	@Override
	public Object getConstraintBA(String name)
	{
		if (constraining)
		{
			return ((Constraining) javaConverter).getConstraintBA(name);
		}
		else
		{
			return null;
		}
	}

	@Override
	public String[] getConstraintNamesAB()
	{
		if (constraining)
		{
			return ((Constraining) javaConverter).getConstraintNamesAB();
		}
		else
		{
			return new String[0];
		}
	}

	@Override
	public String[] getConstraintNamesBA()
	{
		if (constraining)
		{
			return ((Constraining) javaConverter).getConstraintNamesBA();
		}
		else
		{
			return null;
		}
	}

}
