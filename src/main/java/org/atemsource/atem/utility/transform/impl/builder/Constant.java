/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this fi in
 * compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LIC Unless
 * required by applicable law or agreed to in writing, software distributed under the License is distr an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License specific language governing
 * permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.impl.transformation.GenericAttributeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Thie builder creates a single target attribute with a constant value.
 */
@Component
@Scope("prototype")
public class Constant<A, B> extends AbstractAttributeTransformationBuilder<A, B, GenericTransformationBuilder<A, B>>
{
	private Object constantValue;

	private String targetAttribute;

	private Class<?> targetClass;

	@Override
	public void build(EntityTypeBuilder entityTypeBuilder)
	{
		Type<?> attributeTargetType;
		Type[] validTypes;
		attributeTargetType = entityTypeRepository.getType(targetClass);
		SingleAttribute<?> attribute = entityTypeBuilder.addSingleAttribute(targetAttribute, targetClass);
	}

	@Override
	public AttributeTransformation<A, B> create(EntityType<B> targetType)
	{
		GenericAttributeTransformation<A, B> instance = beanLocator.getInstance(GenericAttributeTransformation.class);
		Set<AttributePath> attributeAs = Collections.EMPTY_SET;
		instance.setAttributeAs(attributeAs);
		Set<AttributePath> attributeBs = new HashSet<AttributePath>();
		final AttributePath targetPath = attributePathBuilderFactory.createAttributePath(targetAttribute, targetType);
		attributeBs.add(targetPath);
		instance.setAttributeBs(attributeBs);
		instance.setTransformation(new JavaTransformation<A, B>() {

			@Override
			public void mergeAB(A a, B b, TransformationContext ctx)
			{
				targetPath.setValue(b, constantValue);
			}

			@Override
			public void mergeBA(B b, A a, TransformationContext ctx)
			{
			}
		});
		return instance;

	}

	@Override
	public GenericTransformationBuilder<A, B> from(String attributePath)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public A fromMethod()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * define the name of the target attribute.
	 */
	public Constant<A, B> to(String targetAttribute)
	{
		this.targetAttribute = targetAttribute;
		return this;
	}

	/**
	 * define the type and value of the target attribute.
	 */
	public <T> Constant<A, B> value(Class<T> targetClass, final T constantValue)
	{
		this.constantValue = constantValue;
		this.targetClass = targetClass;
		return this;
	}

}
