/*******************************************************************************
 * Stefan Meyer, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.common;

import java.util.HashMap;
import java.util.Map;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.Type;


public abstract class AttributeOperationBuilder<P extends AttributeOperation, O extends EntityOperation, A extends AttributeOperationBuilder<P, O, A, V>, V extends EntityTypeOperationBuilder<A, V, O, P>>
{
	private EntityTypeOperationBuilder<A, V, O, P> parent;

	private Attribute attribute;
	
	private EntityOperationSelector<O> selector = new EntityOperationSelector<O>();


	private V entityOperationBuilder;

	public V cascade()
	{
		this.entityOperationBuilder = parent.cascade(this);
		return this.entityOperationBuilder;
	}

	public void cascade(O entityOperation)
	{
		this.selector.setDefaultOperation(entityOperation);
	}
	
	public void cascadeDynamically(Type<?> type,O entityOperation)
	{
		this.selector.put(type, entityOperation);
	}
	
	public void cascadeDynamically(Type<?> type,EntityOperationReference<O> ref)
	{
		this.selector.put(type, ref);
	}
	
	protected abstract P createOperation();

	public Attribute getAttribute()
	{
		return attribute;
	}

	protected EntityOperationSelector<O> createEntityOperation()
	{
		if (entityOperationBuilder!=null) {
			O entityOperation = entityOperationBuilder.create();
			selector.setDefaultOperation(entityOperation);
		}
		return selector;
	}

	public void setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}

	public void setParent(EntityTypeOperationBuilder parent)
	{
		this.parent = parent;
	}
}
