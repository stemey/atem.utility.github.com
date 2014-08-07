package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;

public abstract  class AbstractOneToOneAttributeTransformation<A, B>  extends AbstractAttributeTransformation<A, B> implements OneToOneAttributeTransformation<A, B>{

	private Transformation transformation;

	public void mergeBA(B b, A a, TransformationContext ctx) {
		UniTransformation baConverter = transformation == null ? null : transformation.getBA();
		transformInternally(b, a,
				getAttributeB(), getAttributeA(), ctx, baConverter);
	}
	
	public A convertBA(B b, TransformationContext ctx) {
		if (transformation!=null) {
			return (A) transformation.getBA().convert(b, ctx);
		}else{
			return (A) b;
		}
	}
	
	public B convertAB(A a , TransformationContext ctx) {
		if (transformation!=null) {
			return (B) transformation.getAB().convert(a, ctx);
		}else{
			return (B) a;
		}
	}

	public AttributePath getAttributeA() {
		return getAttributeAs().iterator().next();
	}

	public AttributePath getAttributeB() {
		return getAttributeBs().iterator().next();
	}

	public void mergeAB(A a, B b, TransformationContext ctx) {
		UniTransformation abConverter = transformation == null ? null : transformation.getAB();
		transformInternally(a, b,
				getAttributeA(), getAttributeB(), ctx, abConverter);
	}

	/* (non-Javadoc)
	 * @see org.atemsource.atem.utility.transform.impl.transformation.OneToOneAttributeTransformation#getTransformation()
	 */
	@Override
	public Transformation getTransformation() {
		return transformation;
	}

	public void setAttributeA(AttributePath source) {
		Set<AttributePath> attributeAs = new HashSet<AttributePath>();
		attributeAs.add(source);
		setAttributeAs(attributeAs);
	}

	public void setAttributeB(AttributePath attributeB) {
		Set<AttributePath> attributeBs = new HashSet<AttributePath>();
		attributeBs.add(attributeB);
		setAttributeBs(attributeBs);
	}

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	protected abstract void transformInternally(Object a, Object b,
			AttributePath attributeA, AttributePath attributeB, TransformationContext ctx, UniTransformation<Object, Object> ab);

}
