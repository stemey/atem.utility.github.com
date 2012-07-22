/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import net.sf.cglib.proxy.Enhancer;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.DerivedAttribute;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;

public class AbstractAttributeTransformationBuilder<A, B> implements
		AttributeTransformationBuilder<A, B> {

	private AttributePathBuilder attributePathBuilder;

	@Inject
	protected AttributePathBuilderFactory attributePathBuilderFactory;

	@Inject
	protected BeanLocator beanLocator;

	private Converter<A, B> converter;

	private ConverterFactory converterFactory;

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	protected Map<String, Object> meta = new HashMap<String, Object>();

	protected String sourceAttribute;

	protected EntityType<A> sourceType;

	protected String targetAttribute;

	public AbstractAttributeTransformationBuilder() {
		super();
	}

	protected void addDerivation(Transformation<?, ?> transformation,
			Attribute newAttribute, Attribute sourceAttribute) {
		DerivedAttribute derivedAttribute = new DerivedAttribute();
		derivedAttribute.setOriginalAttribute(sourceAttribute);
		derivedAttribute.setTransformation(transformation);
		Attribute metaAttribute = entityTypeRepository.getEntityType(
				Attribute.class).getMetaAttribute(
				DerivationMetaAttributeRegistrar.DERIVED_FROM);
		if (metaAttribute != null) {
			metaAttribute.setValue(newAttribute, derivedAttribute);
		}
	}

	@Override
	public AttributeTransformationBuilder<A, B> convert(
			Converter<A, B> converter) {
		this.converter = converter;
		return this;
	}

	@Override
	public AttributeTransformationBuilder<A, B> convertDynamically(
			JavaUniConverter<String, String> typeCodeConverter) {
		Attribute metaAttribute = entityTypeRepository.getEntityType(
				EntityType.class).getMetaAttribute(
				DerivationMetaAttributeRegistrar.DERIVED_FROM);
		if (metaAttribute == null) {
			throw new IllegalStateException(
					"cannot convert dynamically if metaAttribute is missing");
		}
		this.converter = new DynamicTransformation(typeCodeConverter,
				sourceType, entityTypeRepository,
				(SingleAttribute) metaAttribute);
		return this;
	}

	@Override
	public AttributeTransformationBuilder<A, B> from(String sourceAttribute) {
		this.sourceAttribute = sourceAttribute;
		return this;
	}

	@Override
	public A fromMethod() {
		Enhancer enhancer = new Enhancer();
		return (A) enhancer.create(sourceType.getJavaType(), new PathRecorder(
				this));
	}

	protected Converter<A, B> getConverter(Type<?> type) {
		if (converter != null) {
			return converter;
		} else {
			return (Converter<A, B>) converterFactory.get(type);
		}
	}

	public ConverterFactory getConverterFactory() {
		return converterFactory;
	}

	public AttributePathBuilder getSourcePathBuilder() {
		if (attributePathBuilder == null) {
			attributePathBuilder = attributePathBuilderFactory.createBuilder();
			attributePathBuilder.start(null, sourceType);
		}
		return attributePathBuilder;
	}

	@Override
	public AttributeTransformationBuilder<A, B> metaValue(String name,
			Object metaData) {
		this.meta.put(name, metaData);
		return this;
	}

	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}

	public void setSourceType(EntityType<A> sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	public AttributeTransformationBuilder<A, B> to(String targetAttribute) {
		this.targetAttribute = targetAttribute;
		return this;
	}

}
