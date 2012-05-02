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

import org.atemsource.atem.api.attribute.Attribute;


public class AttributeOperation<E extends EntityOperation>
{
	private Attribute attribute;

	private E entityOperation;

	public Attribute getAttribute()
	{
		return attribute;
	}

	public E getEntityOperation()
	{
		return entityOperation;
	}

	public void setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
	}

	public void setEntityOperation(E entityOperation)
	{
		this.entityOperation = entityOperation;
	}

	@Override
	public String toString()
	{
		return toString("");
	}

	public String toString(String indent)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(indent);
		builder.append("-");
		builder.append(attribute);
		builder.append("\n");
		if (entityOperation != null)
		{
			builder.append(entityOperation.toString(indent + " "));
		}
		return builder.toString();
	}

}
