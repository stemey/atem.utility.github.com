package org.atemsource.atem.utility.binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.domain.DomainA;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public class Binder extends AbstractBinder implements BindingListener {

	public TypeNameConverter getTypeNameConverter() {
		return typeNameConverter;
	}

	public void setTypeNameConverter(TypeNameConverter typeNameConverter) {
		this.typeNameConverter = typeNameConverter;
	}

	private TypeNameConverter typeNameConverter;

	public Binder() {
		super();
	}

	@PostConstruct
	public void process() {
		BindingSession bindingSession = beanLocator
				.getInstance(getBindingSessionClass());
		bindingSession.setTypeNameConverter(typeNameConverter);
		bindingSession.setAttributeNameConverter(attributeNameConverter);
		bindingSession.setSubRepository(subRepository);
		bindingSession
				.setTransformationBuilderFactory(transformationBuilderFactory);
		bindingSession.addListener(this);
		bindingSession.process(getSourceTypes());

	}

	@Override
	public void finished(EntityTypeTransformation<?, ?> transformation) {
	}

	public <A, B> EntityTypeTransformation<A, B> getTransformation(
			Class<A> sourceClass) {

		String targetTypeCode = typeNameConverter.convert(entityTypeRepository
				.getEntityType(sourceClass));
		 EntityType<Object> entityType = entityTypeRepository.getEntityType(targetTypeCode);
		 SingleAttribute<DerivedType> metaAttribute = (SingleAttribute<DerivedType>) entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute(DerivationMetaAttributeRegistrar.DERIVED_FROM);
		 return (EntityTypeTransformation<A, B>) metaAttribute.getValue(entityType).getTransformation();
	}

}