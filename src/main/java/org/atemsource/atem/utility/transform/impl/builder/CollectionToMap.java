package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionToMapAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* This builder creates a transformation from a source collection attribute to a target map attribute.
*/
@Component
@Scope("prototype")
public class CollectionToMap extends OneToOneAttributeTransformationBuilder<Object, Object, CollectionToMap>
{

	private boolean convertNullToEmpty;

	private Converter<?, ?> keyConverter;

	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		Type<?> attributeTargetType;
		if (getConverter(sourcePath.getTargetType().getType()) != null)
		{
			attributeTargetType = getConverter(sourcePath.getTargetType().getType()).getTypeB();
		}
		else
		{
			attributeTargetType = sourcePath.getTargetType().getType();
		}
		entityTypeBuilder.addMapAssociationAttribute(getTargetAttribute(), keyConverter.getTypeB(), attributeTargetType,
			false);
	}

	@Override
	public AttributeTransformation<Object, Object> create(EntityType<Object> targetType)
	{
		CollectionToMapAttributeTransformation transformation =
			beanLocator.getInstance(CollectionToMapAttributeTransformation.class);
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		AttributePath targetPath = attributePathBuilderFactory.createAttributePath(getTargetAttribute(), targetType);
		transformation.setAttributeA(sourcePath);
		transformation.setAttributeB(targetPath);
		transformation.setTransformation(getTransformation(sourcePath.getTargetType().getType()));
		transformation.setTypeA(sourceType);
		transformation.setConvertNullToEmpty(convertNullToEmpty);
		transformation.setTypeB(targetType);
		transformation.setMeta(meta);
		transformation.setKeyConverter(keyConverter);
		transformation.setKeyType(keyConverter.getTypeB());
		addDerivation(transformation, targetPath.getAttribute(), sourcePath.getAttribute());
		return transformation;
	}
/**
* define the converter to extract the key from a source collection element. 
*/
	public CollectionToMap keyConvert(Converter<?, ?> keyConverter)
	{
		this.keyConverter = keyConverter;
		return this;
	}

}
