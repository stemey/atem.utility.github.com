package org.atemsource.atem.utility.binding;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

/**
* This Binder binds each source type to exactly one target type.
*/
public class Binder extends AbstractBinder implements BindingListener
{

	private TypeNameConverter typeNameConverter;

	public Binder()
	{
		super();
		attributeConverters=new ArrayList<AttributeConverter>();
		attributeConverters.add(new AnnotationAttributeConverter());
	}

	@Override
	public void finished(EntityTypeTransformation<?, ?> transformation)
	{
     // we get the actual transformations from the types meta data.
	}

	public <A, B> EntityTypeTransformation<A, B> getTransformation(Class<A> sourceClass)
	{

		String targetTypeCode = typeNameConverter.convert(entityTypeRepository.getEntityType(sourceClass));
		EntityType<Object> entityType = entityTypeRepository.getEntityType(targetTypeCode);
		if (entityType==null) {
			throw new IllegalArgumentException("class "+sourceClass+" was not transformed by this binder ");
		}
		SingleAttribute<DerivedType> metaAttribute =
			(SingleAttribute<DerivedType>) entityTypeRepository.getEntityType(EntityType.class).getMetaAttribute(
				DerivedObject.META_ATTRIBUTE_CODE);
		return (EntityTypeTransformation<A, B>) metaAttribute.getValue(entityType).getTransformation();
	}

	public TypeNameConverter getTypeNameConverter()
	{
		return typeNameConverter;
	}

	@PostConstruct
	public void process()
	{
		BindingSession bindingSession = (BindingSession) beanLocator.getInstance(getBindingSessionClass());
		bindingSession.setTypeNameConverter(typeNameConverter);
		bindingSession.setAttributeNameConverter(attributeNameConverter);
		bindingSession.setAttributeConverters(attributeConverters);
		bindingSession.setSubRepository(subRepository);
		bindingSession.setTransformationBuilderFactory(transformationBuilderFactory);
		bindingSession.addListener(this);
		bindingSession.process(getTypeFilter().getEntityTypes());

	}
/**
* Define the target type name creation strategy.
*/
	public void setTypeNameConverter(TypeNameConverter typeNameConverter)
	{
		this.typeNameConverter = typeNameConverter;
	}

}
