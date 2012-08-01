package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;

public class DynamicUniTransformation<A, B> implements UniTransformation<A, B> {

	private boolean aUnkown;

	private SingleAttribute<DerivedType> derivedTypeAttribute;

	private EntityTypeRepository entityTypeRepository;

	private Type type;

	private TypeNameConverter typeCodeConverter;

	public DynamicUniTransformation(TypeNameConverter typeCodeConverter,
			EntityTypeRepository entityTypeRepository, Type<?> type,
			boolean aUnknown, SingleAttribute<DerivedType> derivedTypeAttribute) {
		this.typeCodeConverter = typeCodeConverter;
		this.type = type;
		this.aUnkown = aUnknown;
		this.entityTypeRepository = entityTypeRepository;
		this.derivedTypeAttribute = derivedTypeAttribute;

	}

	@Override
	public B convert(A a, TransformationContext ctx) {
		if (a == null) {
			return null;
		} else {
			EntityType entityType = entityTypeRepository.getEntityType(a);
			EntityType targetType = entityTypeRepository
					.getEntityType(typeCodeConverter.convert(entityType));
			DerivedType derivedType = derivedTypeAttribute.getValue(targetType);
			UniTransformation transformation = derivedType.getTransformation()
					.getAB();
			return (B) transformation.convert(a, ctx);
		}
	}

	@Override
	public Type<A> getSourceType() {
		if (aUnkown) {
			return null;
		} else {
			return type;
		}
	}

	@Override
	public Type<B> getTargetType() {
		if (aUnkown) {
			return type;
		} else {
			return null;
		}
	}

	@Override
	public B merge(A a, B b, TransformationContext ctx) {
		if (a == null) {
			return null;
		} else {
			EntityType entityType = entityTypeRepository.getEntityType(a);
			EntityType targetType = entityTypeRepository
					.getEntityType(typeCodeConverter.convert(entityType));
			DerivedType derivedType = derivedTypeAttribute.getValue(targetType);
			UniTransformation transformation = derivedType.getTransformation()
					.getAB();
			return (B) transformation.merge(a, b, ctx);
		}
	}

}
