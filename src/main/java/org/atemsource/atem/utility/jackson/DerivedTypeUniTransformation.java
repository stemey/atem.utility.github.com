package org.atemsource.atem.utility.jackson;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class DerivedTypeUniTransformation<A, B> implements UniTransformation<A, B>
{

	private static final UniTransformation NullTransformation = new UniTransformation() {

		@Override
		public Object convert(Object a)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Type getSourceType()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Type getTargetType()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object merge(Object a, Object b)
		{
			// TODO Auto-generated method stub
			return null;
		}
	};

	private boolean ab = true;

	private SingleAttribute<DerivedType> derivedTypeAttribute;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Type<A> sourceType;

	private Type<B> targetType;

	private JavaUniConverter<String, String> typeCodeConverter;

	@Override
	public B convert(A a)
	{
		UniTransformation<A, B> transformation = getUniTransformation(a);
		return transformation.convert(a);
	}

	public SingleAttribute<DerivedType> getDerivedTypeAttribute()
	{
		return derivedTypeAttribute;
	}

	public Type<A> getSourceType()
	{
		return sourceType;
	}

	public Type<B> getTargetType()
	{
		return targetType;
	}

	private UniTransformation<A, B> getUniTransformation(A a)
	{
		if (a == null)
		{
			return NullTransformation;
		}
		ListUniTransformation<A, B> listTransformation = new ListUniTransformation<A, B>();

		EntityType<A> entityType = entityTypeRepository.getEntityType(a);
		while (entityType != null)
		{
			String typeCode = typeCodeConverter.convert(entityType.getCode());
			EntityType<Object> targetType = entityTypeRepository.getEntityType(typeCode);
			DerivedType value = derivedTypeAttribute.getValue(targetType);
			UniTransformation<A, B> transformation;
			if (ab)
			{
				transformation = (UniTransformation<A, B>) value.getTransformation().getAB();
			}
			else
			{
				transformation = (UniTransformation<A, B>) value.getTransformation().getBA();
			}
			listTransformation.addTransformation(transformation);
			entityType = entityType.getSuperEntityType();
		}
		return listTransformation;
	}

	public boolean isAb()
	{
		return ab;
	}

	@Override
	public B merge(A a, B b)
	{
		return getUniTransformation(a).merge(a, b);
	}

	public void setAb(boolean ab)
	{
		this.ab = ab;
	}

	public void setDerivedTypeAttribute(SingleAttribute<DerivedType> derivedTypeAttribute)
	{
		this.derivedTypeAttribute = derivedTypeAttribute;
	}

	public void setSourceType(Type<A> sourceType)
	{
		this.sourceType = sourceType;
	}

	public void setTargetType(Type<B> targetType)
	{
		this.targetType = targetType;
	}

	public void setTypeCodeConverter(JavaUniConverter<String, String> typeCodeConverter)
	{
		this.typeCodeConverter = typeCodeConverter;
	}

}
