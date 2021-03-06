/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;

/**
* This class creates a builder for transformations. 
*
*/
public class TransformationBuilderFactory
{
	@Inject
	private BeanLocator beanLocator;

	private ConverterFactory converterFactory;

	@Deprecated
	public <A, B> TypeTransformationBuilder<A, B> create()
	{
		TypeTransformationBuilder<A, B> transformationBuilder = beanLocator.getInstance(TypeTransformationBuilder.class);
		transformationBuilder.setConverterFactory(converterFactory);
	return transformationBuilder;
	}

/**
* create a TypeTransformationBuilder for a trnasformation from a sourceType to a target type that is defined by the transformation.
*/
	public <A, B> TypeTransformationBuilder<A, B> create(Class<A> sourceType, EntityTypeBuilder targetTypeBuilder)
	{
		TypeTransformationBuilder<A, B> transformationBuilder = beanLocator.getInstance(TypeTransformationBuilder.class);
		transformationBuilder.setSourceType(sourceType);
		transformationBuilder.setTargetTypeBuilder(targetTypeBuilder);
		transformationBuilder.setConverterFactory(converterFactory);
		return transformationBuilder;
	}

/**
* create a TypeTransformationBuilder for a trnasformation from a sourceType to a target type that is defined by the transformation.
*/
	public <A, B> TypeTransformationBuilder<A, B> create(EntityType<A> sourceType, EntityTypeBuilder targetTypeBuilder)
	{
		TypeTransformationBuilder<A, B> transformationBuilder = beanLocator.getInstance(TypeTransformationBuilder.class);
		transformationBuilder.setSourceType(sourceType);
		transformationBuilder.setTargetTypeBuilder(targetTypeBuilder);
		transformationBuilder.setConverterFactory(converterFactory);
		return transformationBuilder;
	}
	
	public <A, B> DynamicTypeTransformationBuilder<A, B> create(EntityTypeBuilder sourceTypeBuilder, EntityTypeBuilder targetTypeBuilder)
	{
		DynamicTypeTransformationBuilder<A, B> transformationBuilder = beanLocator.getInstance(DynamicTypeTransformationBuilder.class);
		transformationBuilder.setSourceTypeBuilder(sourceTypeBuilder);
		transformationBuilder.setTargetTypeBuilder(targetTypeBuilder);
		transformationBuilder.setConverterFactory(converterFactory);
		return transformationBuilder;
	}

	public ConverterFactory getConverterFactory()
	{
		return converterFactory;
	}

	public void setConverterFactory(ConverterFactory converterFactory)
	{
		this.converterFactory = converterFactory;
	}

}
