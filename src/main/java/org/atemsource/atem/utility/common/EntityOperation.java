/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.common;

import java.util.Set;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeVisitor;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;


public class EntityOperation<A extends AttributeOperation> implements View
{
	private Set<A> attributeOperations;

	private EntityType entityType;

	public Set<A> getAttributeOperations()
	{
		return attributeOperations;
	}

	public EntityType getEntityType()
	{
		return entityType;
	}

	public void setAttributeOperations(Set<A> attributeOperations)
	{
		this.attributeOperations = attributeOperations;
	}

	public void setEntityType(EntityType entityType)
	{
		this.entityType = entityType;
	}

	@Override
	public String toString()

	{
		return toString("");
	}

	public String toString(String indent)
	{
		StringBuilder builder = new StringBuilder();
		indent = indent + "   ";
		builder.append(indent);
		builder.append("type: ");
		builder.append(entityType.getCode());
		builder.append("\n");
		for (A attributeViewing : attributeOperations)
		{
			builder.append(attributeViewing.toString(indent));
			// builder.append("\n");
		}
		return builder.toString();
	}

	public <C> void visit(ViewVisitor<C> visitor, C context)
	{
		for (A attributeViewing : attributeOperations)
		{
			Attribute attribute = attributeViewing.getAttribute();
			if (attributeViewing.getEntityOperation(attribute.getTargetType()) != null)
			{
				visitor.visit(context, attribute, new AttributeVisitor<C>(visitor, attributeViewing.getEntityOperation(attribute.getTargetType())));
			}
			else
			{
				visitor.visit(context, attribute);

			}
		}
	}

}
