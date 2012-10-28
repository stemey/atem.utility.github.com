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
package org.atemsource.atem.utility.path;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.OrderableCollection;


public class IndexPathElement<J> implements AttributePathElement
{
	private int index;

	private OrderableCollection<J, ?> attribute;

	public IndexPathElement(int index, OrderableCollection<J, ?> attribute)
	{
		super();
		this.index = index;
		this.attribute = attribute;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndexPathElement other = (IndexPathElement) obj;
		if (attribute == null)
		{
			if (other.attribute != null)
				return false;
		}
		else if (!attribute.equals(other.attribute))
			return false;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public Attribute getAttribute()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return String.valueOf(index);
	}

	@Override
	public PathType getSourceType()
	{
		return new PathType(Cardinality.INDEXED, attribute.getEntityType());
	}

	@Override
	public PathType getTargetType()
	{
		return new PathType(Cardinality.SINGLE, attribute.getTargetType());
	}

	@Override
	public Object getValue(Object entity)
	{
		return attribute.getElement(entity, index);
	}

	@Override
	public int hashCode()
	{
		return getName().hashCode();
	}

	@Override
	public boolean isIndexOrKey()
	{
		return true;
	}

	@Override
	public void setValue(Object entity, Object value)
	{
		attribute.removeElement(entity, index);
		attribute.addElement(entity, index, (J) value);
	}

}
