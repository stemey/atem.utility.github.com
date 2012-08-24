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

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.AttributeTransformation;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.CustomAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.DerivedAttribute;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.impl.DerivationMetaAttributeRegistrar;

public abstract class AbstractAttributeTransformationBuilder<A, B, T extends AbstractAttributeTransformationBuilder<A, B, T>>
		implements CustomAttributeTransformationBuilder<A, B, T> {

	private AttributePathBuilder attributePathBuilder;

	@Inject
	protected AttributePathBuilderFactory attributePathBuilderFactory;

	@Inject
	protected BeanLocator beanLocator;

	protected ConverterFactory converterFactory;

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	protected Map<String, Object> meta = new HashMap<String, Object>();

	protected EntityType<A> sourceType;

	public EntityType<A> getSourceType() {
		return sourceType;
	}

	public AbstractAttributeTransformationBuilder() {
		super();
	}

	protected void addDerivation(AttributeTransformation<?, ?> transformation,
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
	public T metaValue(String name, Object metaData) {
		this.meta.put(name, metaData);
		return (T) this;
	}

	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}

	public void setSourceType(EntityType<A> sourceType) {
		this.sourceType = sourceType;
	}

	private EntityTypeBuilder targetTypeBuilder;

	public EntityTypeBuilder getTargetTypeBuilder() {
		return targetTypeBuilder;
	}

	public void setTargetTypeBuilder(EntityTypeBuilder targetTypeBuilder) {
		this.targetTypeBuilder = targetTypeBuilder;
	}

}
