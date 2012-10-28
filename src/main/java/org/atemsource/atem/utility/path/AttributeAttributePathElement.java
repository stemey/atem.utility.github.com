/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.path;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.path.Cardinality;


public class AttributeAttributePathElement implements AttributePathElement
{
	private Attribute attribute;

	public AttributeAttributePathElement(Attribute attribute)
	{
		super();
		if (attribute == null)
		{
			throw new NullPointerException("attribute is null");
		}
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
		AttributeAttributePathElement other = (AttributeAttributePathElement) obj;
		if (attribute == null)
		{
			if (other.attribute != null)
				return false;
		}
		else if (!attribute.equals(other.attribute))
			return false;
		return true;
	}

	public Attribute getAttribute()
	{
		return attribute;
	}

	@Override
	public String getName()
	{
		return attribute.getCode();
	}

	@Override
	public PathType getSourceType()
	{
		return new PathType(Cardinality.SINGLE, attribute.getEntityType());
	}

	@Override
	public PathType getTargetType()
	{
		if (attribute instanceof MapAttribute)
		{
			return new PathType(Cardinality.MAP, attribute.getTargetType());
		}
		else if (attribute instanceof CollectionAttribute)

		{
			CollectionSortType collectionSortType = ((CollectionAttribute) attribute).getCollectionSortType();
			if (collectionSortType == null)
			{
				return new PathType(Cardinality.SET, attribute.getTargetType());
			}
			switch (collectionSortType)
			{
				case NONE:
					return new PathType(Cardinality.SET, attribute.getTargetType());
				case ORDERABLE:
					return new PathType(Cardinality.INDEXED, attribute.getTargetType());
				case SORTED:
					return new PathType(Cardinality.INDEXED, attribute.getTargetType());
			}
			throw new IllegalStateException("no sort type defined");
		}
		else
		{
			return new PathType(Cardinality.SINGLE, attribute.getTargetType());
		}
	}

	@Override
	public Object getValue(Object entity)
	{
		return attribute.getValue(entity);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
		return result;
	}

	@Override
	public boolean isIndexOrKey()
	{
		return false;
	}

	@Override
	public void setValue(Object entity, Object value)
	{
		attribute.setValue(entity, value);
	}
}
