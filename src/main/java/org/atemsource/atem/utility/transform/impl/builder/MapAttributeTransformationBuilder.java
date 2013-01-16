/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.impl.transformation.MapAssociationAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* this builder creates a transformation from one map attribute to a target map attribute.
*/
@Component
@Scope("prototype")
public class MapAttributeTransformationBuilder<A, B> extends
	OneToOneAttributeTransformationBuilder<A, B, MapAttributeTransformationBuilder<A, B>>
{
	private boolean convertNullToEmpty;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private Converter<?, ?> keyConverter;

	private boolean sorted;

	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		Type<?> attributeTargetType;
		Converter<?, ?> converter = getConverter(sourcePath.getTargetType().getType());
		attributeTargetType = getTargetType(sourcePath, converter);
		Type[] validTypes = getValidTargetTypes(sourcePath, converter);
		Type<?> keyType;
		if (keyConverter != null)
		{
			keyType = keyConverter.getTypeB();
		}
		else
		{
			keyType = ((MapAttribute) sourcePath.getAttribute()).getKeyType();
		}

		entityTypeBuilder.addMapAssociationAttribute(getTargetAttribute(), keyType, attributeTargetType, sorted,
			validTypes);
	}
/**
* defined the key converter
*/
	public MapAttributeTransformationBuilder convertKey(Converter<?, ?> keyConverter)
	{
		this.keyConverter = keyConverter;
		return this;
	}
/*
* convert a null source value to an empty target map.
*/
	public MapAttributeTransformationBuilder convertNullToEmpty()
	{
		convertNullToEmpty = true;
		return this;
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		AttributePath targetPath = attributePathBuilderFactory.createAttributePath(getTargetAttribute(), targetType);
		MapAssociationAttributeTransformation<A, B> transformation =
			beanLocator.getInstance(MapAssociationAttributeTransformation.class);
		transformation.setAttributeA(sourcePath);
		transformation.setAttributeB(targetPath);
		transformation.setTransformation(getTransformation(sourcePath.getTargetType().getType()));
		transformation.setTypeA(sourceType);
		transformation.setConvertNullToEmpty(convertNullToEmpty);
		transformation.setTypeB(targetType);
		transformation.setMeta(meta);
		transformation.setKeyConverter(keyConverter);
		addDerivation(transformation, targetPath.getAttribute(), sourcePath.getAttribute());
		return transformation;
	}
/**
* the target map should be sorted.
*/
	public void sorted()
	{
		sorted = true;
	}

}
