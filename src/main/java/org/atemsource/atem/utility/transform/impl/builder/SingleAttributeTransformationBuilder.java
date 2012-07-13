/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.impl.transformation.SingleAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class SingleAttributeTransformationBuilder extends AbstractAttributeTransformationBuilder implements
	TransformationBuilder, AttributeTransformationBuilder
{
	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(sourceAttribute, sourceType);
		Type<?> attributeTargetType;
		if (getConverter(sourcePath.getTargetType().getType()) != null)
		{
			attributeTargetType = getConverter(sourcePath.getTargetType().getType()).getTypeB();
		}
		else
		{
			attributeTargetType = sourcePath.getTargetType().getType();
		}
		entityTypeBuilder.addSingleAttribute(targetAttribute, attributeTargetType);
	}

	@Override
	public Transformation<?, ?> create(EntityType<?> targetType)
	{
		AttributePath sourcePath = attributePathBuilderFactory.createAttributePath(sourceAttribute, sourceType);
		AttributePath targetPath = attributePathBuilderFactory.createAttributePath(targetAttribute, targetType);
		SingleAttributeTransformation<?, ?> primitiveAttributeTransformation =
			beanLocator.getInstance(SingleAttributeTransformation.class);
		primitiveAttributeTransformation.setAttributeA(sourcePath);
		primitiveAttributeTransformation.setAttributeB(targetPath);
		primitiveAttributeTransformation.setConverter(getConverter(sourcePath.getTargetType().getType()));
		primitiveAttributeTransformation.setTypeA(sourceType);
		primitiveAttributeTransformation.setTypeB(targetType);
		primitiveAttributeTransformation.setMeta(meta);
		addDerivation(primitiveAttributeTransformation, targetPath.getAttribute(), sourcePath.getAttribute());
		return primitiveAttributeTransformation;
	}

}
