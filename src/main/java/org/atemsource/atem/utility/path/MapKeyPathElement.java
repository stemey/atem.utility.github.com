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
import org.atemsource.atem.api.attribute.MapAttribute;


public class MapKeyPathElement implements AttributePathElement
{

	private Object key;

	private MapAttribute attribute;

	public MapKeyPathElement(Object key, MapAttribute attribute)
	{
		super();
		this.key = key;
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
		MapKeyPathElement other = (MapKeyPathElement) obj;
		if (attribute == null)
		{
			if (other.attribute != null)
				return false;
		}
		else if (!attribute.equals(other.attribute))
			return false;
		if (key == null)
		{
			if (other.key != null)
				return false;
		}
		else if (!key.equals(other.key))
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
		return String.valueOf(key);
	}

	@Override
	public PathType getSourceType()
	{
		return new PathType(Cardinality.MAP, attribute.getEntityType());
	}

	@Override
	public PathType getTargetType()
	{
		return new PathType(Cardinality.SINGLE, attribute.getTargetType());
	}

	@Override
	public Object getValue(Object entity)
	{
		return attribute.getElement(entity, key);
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
		attribute.putElement(entity, key, value);
	}

}
