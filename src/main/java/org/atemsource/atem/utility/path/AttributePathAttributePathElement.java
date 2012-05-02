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


public class AttributePathAttributePathElement implements AttributePathElement
{
	private AttributePath attributePath;

	public AttributePathAttributePathElement(AttributePath attributePath)
	{
		super();
		this.attributePath = attributePath;
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
		AttributePathAttributePathElement other = (AttributePathAttributePathElement) obj;
		if (attributePath == null)
		{
			if (other.attributePath != null)
				return false;
		}
		else if (!attributePath.equals(other.attributePath))
			return false;
		return true;
	}

	@Override
	public Attribute getAttribute()
	{
		return attributePath.getAttribute();
	}

	@Override
	public String getName()
	{
		return attributePath.toString();
	}

	@Override
	public PathType getSourceType()
	{
		return attributePath.getSourceType();
	}

	@Override
	public PathType getTargetType()
	{
		return attributePath.getTargetType();
	}

	@Override
	public Object getValue(Object entity)
	{
		return attributePath.getValue(entity);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributePath == null) ? 0 : attributePath.hashCode());
		return result;
	}

	@Override
	public boolean isIndexOrKey()
	{
		return attributePath.isContainingIndexesOrKeys();
	}

	@Override
	public void setValue(Object entity, Object value)
	{
		attributePath.setValue(entity, value);
	}
}
