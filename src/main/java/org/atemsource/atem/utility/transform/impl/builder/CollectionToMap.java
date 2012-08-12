package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionAssociationAttributeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionToMapAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CollectionToMap extends
		AbstractAttributeTransformationBuilder<Object, Object> {

	private Converter<?, ?> keyConverter;
	private boolean convertNullToEmpty;

	@Override
	public void build(EntityTypeBuilder entityTypeBuilder) {
		AttributePath sourcePath = attributePathBuilderFactory
				.createAttributePath(sourceAttribute, sourceType);
		Type<?> attributeTargetType;
		if (getConverter(sourcePath.getTargetType().getType()) != null) {
			attributeTargetType = getConverter(
					sourcePath.getTargetType().getType()).getTypeB();
		} else {
			attributeTargetType = sourcePath.getTargetType().getType();
		}
		entityTypeBuilder.addMapAssociationAttribute(targetAttribute,
				keyConverter.getTypeB(), attributeTargetType);
	}

	@Override
	public AttributeTransformation<Object,Object> create(EntityType<Object> targetType) {
		CollectionToMapAttributeTransformation transformation = beanLocator
				.getInstance(CollectionToMapAttributeTransformation.class);
		AttributePath sourcePath = attributePathBuilderFactory
				.createAttributePath(sourceAttribute, sourceType);
		AttributePath targetPath = attributePathBuilderFactory
				.createAttributePath(targetAttribute, targetType);
		transformation.setAttributeA(sourcePath);
		transformation.setAttributeB(targetPath);
		transformation.setConverter(getConverter(sourcePath.getTargetType()
				.getType()));
		transformation.setTypeA(sourceType);
		transformation.setConvertNullToEmpty(convertNullToEmpty);
		transformation.setTypeB(targetType);
		transformation.setMeta(meta);
		transformation.setKeyConverter(keyConverter);
		transformation.setKeyType(keyConverter.getTypeB());
		addDerivation(transformation, targetPath.getAttribute(),
				sourcePath.getAttribute());
		return transformation;
	}

	public void keyConvert(Converter<?, ?> keyConverter) {
		this.keyConverter = keyConverter;
	}

}
