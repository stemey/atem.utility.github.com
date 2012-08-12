/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.impl.transformation.CollectionAssociationAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CollectionAttributeTransformationBuilder<A, B> extends
		AbstractAttributeTransformationBuilder<A, B>

{
	private CollectionSortType collectionSortType;

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
		if (collectionSortType == null) {
			collectionSortType = ((CollectionAttribute) sourcePath
					.getAttribute()).getCollectionSortType();
		}
		entityTypeBuilder.addMultiAssociationAttribute(targetAttribute,
				attributeTargetType, collectionSortType);
	}

	public CollectionAttributeTransformationBuilder convertNullToEmpty() {
		convertNullToEmpty = true;
		return this;
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType) {
		AttributePath sourcePath = attributePathBuilderFactory
				.createAttributePath(sourceAttribute, sourceType);
		AttributePath targetPath = attributePathBuilderFactory
				.createAttributePath(targetAttribute, targetType);
		CollectionAssociationAttributeTransformation<A, B> transformation = beanLocator
				.getInstance(CollectionAssociationAttributeTransformation.class);
		transformation.setAttributeA(sourcePath);
		transformation.setAttributeB(targetPath);
		transformation.setConverter(getConverter(sourcePath.getTargetType()
				.getType()));
		transformation.setTypeA(sourceType);
		transformation.setConvertNullToEmpty(convertNullToEmpty);
		transformation.setTypeB(targetType);
		transformation.setMeta(meta);
		addDerivation(transformation, targetPath.getAttribute(),
				sourcePath.getAttribute());
		return transformation;
	}

	public CollectionAttributeTransformationBuilder sort(
			CollectionSortType collectionSortType) {
		this.collectionSortType = collectionSortType;
		return this;
	}

}
