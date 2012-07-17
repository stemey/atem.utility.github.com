/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.snapshot;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class SnapshotBuilder implements ViewVisitor<SnapshotBuilder>
{

	protected AttributePathBuilderFactory attributePathBuilderFactory;

	@Inject
	private BeanLocator beanLocator;

	private DynamicEntityTypeSubrepository<?> dynamicEntityTypeRepository;

	private EntityType<?> entityType;

	private SnapshotBuilderFactory factory;

	private Set<SnapshotAttributeBuilder> subBuilders = new HashSet<SnapshotAttributeBuilder>();

	private TypeTransformationBuilder<Object, Object> transformationBuilder;

	private TransformationBuilderFactory transformationBuilderFactory;

	public Transformation<?, ?> create()
	{
		for (SnapshotAttributeBuilder subBuilder : subBuilders)
		{
			subBuilder.preCreate();
		}
		return transformationBuilder.buildTypeTransformation();
	}

	public AttributePathBuilderFactory getAttributePathBuilderFactory()
	{
		return attributePathBuilderFactory;
	}

	public DynamicEntityTypeSubrepository<?> getDynamicEntityTypeRepository()
	{
		return dynamicEntityTypeRepository;
	}

	private EntityType<?> getEntityType()
	{
		return entityType;
	}

	public SnapshotBuilderFactory getFactory()
	{
		return factory;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory()
	{
		return transformationBuilderFactory;
	}

	public SnapshotAttributeBuilder include(Attribute attribute)
	{
		AttributeTransformationBuilder attributeTransformationBuilder =
			transformationBuilder.transform(attribute.getClass()).from(attribute.getCode()).to(attribute.getCode());
		SnapshotAttributeBuilder attributeBuilder = beanLocator.getInstance(SnapshotAttributeBuilder.class);
		attributeBuilder.setSnapshotBuilderFactory(this.factory);
		attributeBuilder.initialize(attributeTransformationBuilder, attribute.getTargetType());
		subBuilders.add(attributeBuilder);
		return attributeBuilder;
	}

	public SnapshotAttributeBuilder include(String path)
	{
		AttributePath attributePath = attributePathBuilderFactory.createAttributePath(path, entityType);
		String targetAttributePath = path.replace('.', '_');
		AttributeTransformationBuilder attributeTransformationBuilder =
			transformationBuilder.transform(attributePath.getAttribute().getClass()).from(path).to(targetAttributePath);
		SnapshotAttributeBuilder attributeBuilder = beanLocator.getInstance(SnapshotAttributeBuilder.class);
		attributeBuilder.initialize(attributeTransformationBuilder, attributePath.getAttribute().getTargetType());
		subBuilders.add(attributeBuilder);
		return attributeBuilder;
	}

	public void include(View view)
	{
		view.visit(this, this);
	}

	public void initialize()
	{
		transformationBuilder = (TypeTransformationBuilder<Object, Object>) transformationBuilderFactory.create();
		transformationBuilder.setSourceType(entityType);
		transformationBuilder.setTargetTypeBuilder(dynamicEntityTypeRepository.createBuilder("snapshot::"
			+ entityType.getCode()));
	}

	public void setAttributePathBuilderFactory(AttributePathBuilderFactory attributePathBuilderFactory)
	{
		this.attributePathBuilderFactory = attributePathBuilderFactory;
	}

	public void setDynamicEntityTypeRepository(DynamicEntityTypeSubrepository<?> dynamicEntityTypeRepository)
	{
		this.dynamicEntityTypeRepository = dynamicEntityTypeRepository;
	}

	public void setEntityType(EntityType<?> entityType)
	{
		this.entityType = entityType;
	}

	public void setFactory(SnapshotBuilderFactory factory)
	{
		this.factory = factory;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory)
	{
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	@Override
	public void visit(SnapshotBuilder context, Attribute attribute)
	{
		context.include(context.getEntityType().getAttribute(attribute.getCode()));
	}

	@Override
	public void visit(SnapshotBuilder context, Attribute attribute, AttributeVisitor<SnapshotBuilder> attributeVisitor)
	{

		SnapshotBuilder snapshotBuilder =
			context.include(context.getEntityType().getAttribute(attribute.getCode())).cascade();
		attributeVisitor.visit(snapshotBuilder);

	}

	@Override
	public void visitSubView(SnapshotBuilder context, View view)
	{

	}

	@Override
	public void visitSuperView(SnapshotBuilder context, View view)
	{

	}
}
