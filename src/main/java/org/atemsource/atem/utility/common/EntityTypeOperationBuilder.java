/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;


public abstract class EntityTypeOperationBuilder<A extends AttributeOperationBuilder<P, O, A, ?>, V extends EntityTypeOperationBuilder, O extends EntityOperation, P extends AttributeOperation>
{

	private EntityType<?> entityType;

	private final Map<String, A> includedAttributes = new HashMap<String, A>();

	private final List<O> includeSubTypes = new ArrayList<O>();

	private O includeSuperType;

	private EntityOperationReference<O> self;

	public EntityTypeOperationBuilder()
	{
	}

	V cascade(AttributeOperationBuilder child)
	{
		Attribute attribute = child.getAttribute();
		V childViewBuilder = createViewBuilder((EntityType) attribute.getTargetType());
		return childViewBuilder;
	}

	public final O create()
	{
		O operation = createInternally();
		if (self != null)
		{
			self.setOperation(operation);
		}
		operation.setSuperOperation(getSuperOperation());
		operation.setSubOperations(getSubOperations());
		return operation;
	}

	protected abstract A createAttributeOperationBuilder(Attribute attribute);

	protected abstract O createInternally();

	protected Set<P> createOperations()
	{

		Set<P> operations = new HashSet<P>();
		for (A a : includedAttributes.values())
		{
			P attributeOperation = a.createOperation();
			operations.add(attributeOperation);
		}
		return operations;
	}

	protected abstract V createViewBuilder(EntityType<?> targetType);

	public EntityType<?> getEntityType()
	{
		return entityType;
	}

	public EntityOperationReference<O> getReference()
	{
		if (self == null)
		{
			self = new EntityOperationReference<O>();
		}
		return self;
	}

	protected List<O> getSubOperations()
	{
		return includeSubTypes;
	}

	protected O getSuperOperation()
	{
		return includeSuperType;
	}

	public A include(Attribute attribute)
	{
		A a = createAttributeOperationBuilder(attribute);
		a.setAttribute(attribute);
		a.setParent(this);
		includedAttributes.put(attribute.getCode(), a);
		return a;
	}

	public A include(String attributeCode)
	{
		Attribute attribute = entityType.getAttribute(attributeCode);
		if (attribute == null)
		{
			throw new IllegalArgumentException("no attribute " + attributeCode + " found on type " + entityType.getCode());
		}
		return include(attribute);
	}

	public V include(View view)
	{
		view.visit(new ViewVisitor<EntityTypeOperationBuilder>() {

			@Override
			public void visit(EntityTypeOperationBuilder context, Attribute attribute)
			{
				context.include(attribute.getCode());
			}

			@Override
			public void visit(EntityTypeOperationBuilder context, Attribute attribute,
				Visitor<EntityTypeOperationBuilder> visitor)
			{
				EntityTypeOperationBuilder builder = context.include(attribute.getCode()).cascade();
				visitor.visit(builder);
			}

			@Override
			public boolean visitSubView(EntityTypeOperationBuilder context, View view)
			{
				return false;
			}

			@Override
			public boolean visitSuperView(EntityTypeOperationBuilder context, View view)
			{
				return false;
			}
		}, this);
		return (V) this;
	}

	public V includeAll()
	{
		List<Attribute> attributes = entityType.getDeclaredAttributes();
		for (Attribute attribute : attributes)
		{
			include(attribute);
		}
		return (V) this;
	}

	public V includePrimitives(boolean includeSuperTypes)
	{
		List<Attribute> attributes = includeSuperTypes ? entityType.getAttributes() : entityType.getDeclaredAttributes();
		for (Attribute attribute : attributes)
		{
			if (attribute.getTargetType() instanceof PrimitiveType<?> && attribute instanceof SingleAttribute<?>)
			{
				include(attribute);
			}
		}
		return (V) this;
	}

	public V includeSub(O subOperation)
	{
		this.includeSubTypes.add(subOperation);
		return (V) this;
	}

	public V includeSuper(O superOperation)
	{
		this.includeSuperType = superOperation;
		return (V) this;
	}

	public V remove(String attributeCode)
	{
		A removed = includedAttributes.remove(attributeCode);
		if (removed == null)
		{
			throw new IllegalArgumentException(attributeCode + " is not included");
		}
		return (V) this;
	}

	protected void setEntityType(EntityType<?> entityType)
	{
		this.entityType = entityType;
	}

}
