package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;


public class DynamicTransformation<A, B> implements Converter<A, B>
{

	private final DynamicUniTransformation ab;

	private final DynamicUniTransformation ba;

	public DynamicTransformation(TypeNameConverter typeCodeConverter, Type<?> type,
		EntityTypeRepository entityTypeRepository, SingleAttribute<DerivedType> derivedTypeAttribute,
		ConverterFactory converterFactory)
	{
		super();
		ab =
			new DynamicUniTransformation<A, B>(typeCodeConverter, entityTypeRepository, type, false, derivedTypeAttribute,
				converterFactory);
		ba =
			new DynamicUniTransformation<A, B>(typeCodeConverter, entityTypeRepository, type, true, derivedTypeAttribute,
				converterFactory);
	}

	@Override
	public UniConverter<A, B> getAB()
	{
		return ab;
	}

	@Override
	public UniConverter<B, A> getBA()
	{
		return ba;
	}

	@Override
	public Type<?> getTypeA()
	{
		return ab.getSourceType();
	}

	@Override
	public Type<?> getTypeB()
	{
		return null;
	}

}
