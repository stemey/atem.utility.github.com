package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;


public abstract class OneToOneAttributeTransformation<A, B> extends AbstractAttributeTransformation<A, B>
{

	private Transformation transformation;

	protected AttributePath getAttributeA()
	{
		return getAttributeAs().iterator().next();
	}

	protected AttributePath getAttributeB()
	{
		return getAttributeBs().iterator().next();
	}

	public Transformation getTransformation()
	{
		return transformation;
	}

	@Override
	public void mergeAB(A a, B b, TransformationContext ctx)
	{
		try
		{
			UniTransformation abConverter = transformation == null ? null : transformation.getAB();
			transformInternally(a, b, getAttributeA(), getAttributeB(), ctx, abConverter);
		}
		catch (Exception e)
		{
			Object value = getAttributeA().getValue(a);
			throw new TechnicalException("cannot transform " + getAttributeA().getAsString() + " on entity " + a
				+ " with value " + value, e);
		}
	}

	@Override
	public void mergeBA(B b, A a, TransformationContext ctx)
	{
		UniTransformation baConverter = transformation == null ? null : transformation.getBA();
		try
		{
			transformInternally(b, a, getAttributeB(), getAttributeA(), ctx, baConverter);
		}
		catch (Exception e)
		{
			Object value = getAttributeB().getValue(b);
			throw new TechnicalException("cannot transform " + getAttributeB().getAsString() + " with value " + b, e);
		}
	}

	public void setAttributeA(AttributePath source)
	{
		Set<AttributePath> attributeAs = new HashSet<AttributePath>();
		attributeAs.add(source);
		setAttributeAs(attributeAs);
	}

	public void setAttributeB(AttributePath attributeB)
	{
		Set<AttributePath> attributeBs = new HashSet<AttributePath>();
		attributeBs.add(attributeB);
		setAttributeBs(attributeBs);
	}

	public void setTransformation(Transformation transformation)
	{
		this.transformation = transformation;
	}

	protected abstract void transformInternally(Object a, Object b, AttributePath attributeA, AttributePath attributeB,
		TransformationContext ctx, UniTransformation<Object, Object> ab);

}
