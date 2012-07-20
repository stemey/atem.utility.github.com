package org.atemsource.atem.utility.jackson;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class DerivedTypeTransformation<A, B> implements Transformation<A, B>
{
	@Inject
	private DerivedTypeUniTransformation<A, B> ab;

	@Inject
	private DerivedTypeUniTransformation<B, A> ba;

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
	public Type<A> getTypeA()
	{
		return ab.getSourceType();
	}

	@Override
	public Type<B> getTypeB()
	{
		return ab.getTargetType();
	}

	@PostConstruct
	public void initialize()
	{
		ab.setAb(true);
		ba.setAb(false);

	}

	public void setDerivedAttribute(SingleAttribute<DerivedType> attribute)
	{
		ab.setDerivedTypeAttribute(attribute);
		ba.setDerivedTypeAttribute(attribute);
	}

	public void setTypeA(EntityType<A> typeA)
	{
		ab.setSourceType(typeA);
		ba.setTargetType(typeA);
	}

	public void setTypeB(EntityType<B> typeB)
	{
		ba.setSourceType(typeB);
		ab.setTargetType(typeB);
	}

	public void setTypeCodeConverter(JavaUniConverter<String, String> converter)
	{
		ab.setTypeCodeConverter(converter);
		ba.setTypeCodeConverter(converter);
	}

}
