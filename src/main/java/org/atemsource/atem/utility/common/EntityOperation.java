/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.AttributeView;
import org.atemsource.atem.api.view.View;


public class EntityOperation<A extends AttributeOperation, O extends EntityOperation<A, O>> implements View
{
	private Set<A> attributeOperations;

	private EntityType entityType;

	private List<O> subOperations;

	private O superOperation;

	public Set<A> getAttributeOperations()
	{
		return attributeOperations;
	}

	public EntityType getEntityType()
	{
		return entityType;
	}

	protected List<O> getSubOperations()
	{
		return subOperations;
	}

	protected O getSuperOperation()
	{
		return superOperation;
	}

	public void setAttributeOperations(Set<A> attributeOperations)
	{
		this.attributeOperations = attributeOperations;
	}

	public void setEntityType(EntityType entityType)
	{
		this.entityType = entityType;
	}

	public void setSubOperations(List<O> subOperations)
	{
		this.subOperations = subOperations;
	}

	public void setSuperOperation(O superOperation)
	{
		this.superOperation = superOperation;
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

	@Override
	public Iterator<AttributeView> attributes() {
		List<AttributeView> attribues = new ArrayList<AttributeView>(attributeOperations.size());
		for (A a:attributeOperations) {
			attribues.add(new AttributeView(a.getAttribute(),a.getEntityOperation(a.getAttribute().getTargetType())));
		}
		return attribues.iterator();
	}

	@Override
	public View getSuperView() {
		return getSuperOperation();
	}

	@Override
	public Iterator<? extends View> subviews() {
		return subOperations.iterator();
	}

}
