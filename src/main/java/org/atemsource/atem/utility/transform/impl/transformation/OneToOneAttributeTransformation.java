package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;

public abstract  class OneToOneAttributeTransformation<A, B>  extends AbstractAttributeTransformation<A, B>{

	private Converter converter;

	public void mergeBA(B b, A a, TransformationContext ctx) {
		UniConverter baConverter = converter == null ? null : converter.getBA();
		transformInternally(b, a,
				getAttributeB(), getAttributeA(), ctx, baConverter);
	}

	protected AttributePath getAttributeA() {
		return getAttributeAs().iterator().next();
	}

	protected AttributePath getAttributeB() {
		return getAttributeBs().iterator().next();
	}

	public void mergeAB(A a, B b, TransformationContext ctx) {
		UniConverter abConverter = converter == null ? null : converter.getAB();
		transformInternally(a, b,
				getAttributeA(), getAttributeB(), ctx, abConverter);
	}

	public Converter getConverter() {
		return converter;
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

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	protected abstract void transformInternally(Object a, Object b,
			AttributePath attributeA, AttributePath attributeB, TransformationContext ctx, UniConverter<Object, Object> ab);

}
