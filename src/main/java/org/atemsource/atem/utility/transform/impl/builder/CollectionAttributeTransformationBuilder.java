/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.impl.converter.Constraining;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionAssociationAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
* Use this builder for a transformation from on collection attribute to another.
*
*/
@Component
@Scope("prototype")
public class CollectionAttributeTransformationBuilder<A, B> extends
	OneToOneAttributeTransformationBuilder<A, B, CollectionAttributeTransformationBuilder<A, B>>

{
	private CollectionSortType collectionSortType;

	private boolean convertEmptyToNull;

	private boolean convertNullToEmpty;

	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		Type<?> attributeTargetType;
		Converter<?, ?> converter = getConverter(sourcePath.getTargetType().getType());
		attributeTargetType=getTargetType(sourcePath, converter);
		Type[] validTypes=getValidTargetTypes(sourcePath, converter);
		if (collectionSortType == null)
		{
			collectionSortType = ((CollectionAttribute) sourcePath.getAttribute()).getCollectionSortType();
		}
		CollectionAttribute<?, Object> attribute =
			entityTypeBuilder.addMultiAssociationAttribute(getTargetAttribute(), attributeTargetType, validTypes,collectionSortType);
		if (converter != null && converter instanceof Constraining)
		{
			Constraining constraining = ((Constraining) converter);
			for (String name : constraining.getConstraintNamesAB())
			{
				Attribute metaAttribute = entityTypeRepository.getEntityType(Attribute.class).getMetaAttribute(name);
				if (metaAttribute != null)
				{
					metaAttribute.setValue(attribute, constraining.getConstraintAB(name));
				}
			}
		}
	}
/** 
* the if the source collection is empty then the target collection should be null. 
*/
	public CollectionAttributeTransformationBuilder convertEmptyToNull()
	{
		convertEmptyToNull = true;
		return this;
	}

/** 
* the if the source collection is null then the target collection should be empty. 
*/
	public CollectionAttributeTransformationBuilder convertNullToEmpty()
	{
		convertNullToEmpty = true;
		return this;
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(getSourceAttribute(), sourceType);
		AttributePath targetPath = attributePathBuilderFactory.createAttributePath(getTargetAttribute(), targetType);
		CollectionAssociationAttributeTransformation<A, B> transformation =
			beanLocator.getInstance(CollectionAssociationAttributeTransformation.class);
		transformation.setAttributeA(sourcePath);
		transformation.setAttributeB(targetPath);
		transformation.setTransformation(getTransformation(sourcePath.getTargetType().getType()));
		transformation.setTypeA(sourceType);
		transformation.setConvertNullToEmpty(convertNullToEmpty);
		transformation.setConvertEmptyToNull(convertEmptyToNull);
		transformation.setTypeB(targetType);
		transformation.setMeta(meta);
		addDerivation(transformation, targetPath.getAttribute(), sourcePath.getAttribute());
		return transformation;
	}
/**
* define the CollectionSortType of the target collection if it differs from the source collection.s
*/
	public CollectionAttributeTransformationBuilder sort(CollectionSortType collectionSortType)
	{
		this.collectionSortType = collectionSortType;
		return this;
	}

}
