/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.path;

import java.util.ArrayList;
import java.util.List;
import org.atemsource.atem.api.attribute.Attribute;


public class AttributePathImpl implements AttributePath
{

	private final List<AttributePathElement> path;

	public AttributePathImpl()
	{
		super();
		this.path = new ArrayList();
	}

	public AttributePathImpl(Attribute newAttribute)
	{
		path = new ArrayList<AttributePathElement>();
		path.add(new AttributeAttributePathElement(newAttribute));
	}

	AttributePathImpl(List<AttributePathElement> path)
	{
		super();
		this.path = path;
	}

	@Override
	public String getAsString()
	{
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < path.size(); index++)
		{
			if (index > 0)
			{
				builder.append(".");
			}
			AttributePathElement pathElement = path.get(index);
			builder.append(pathElement.getName());
		}
		return builder.toString();
	}

	@Override
	public Attribute getAttribute()
	{
		AttributePathElement element = path.get(path.size() - 1);
		if (element instanceof AttributeAttributePathElement)
		{
			return ((AttributeAttributePathElement) element).getAttribute();
		}
		else
		{
			return null;
		}
	}

	@Override
	public Attribute getAttribute(Object entity)
	{
		if (entity == null)
		{
			return null;
		}
		Attribute attribute = getAttribute();
		Object baseValue = getBaseValue(entity);
		if (baseValue == null)
		{
			return null;
		}
		else if (attribute.getEntityType().isAssignableFrom(entity))
		{
			return attribute;
		}
		else
		{
			return null;
		}

	}

	@Override
	public Object getBaseValue(Object entity)
	{
		for (int index = 0; index < path.size() - 1; index++)
		{
			if (entity == null)
			{
				return null;
			}
			AttributePathElement attribute = path.get(index);
			if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
			{
				entity = attribute.getValue(entity);
			}
			else
			{
				// e.g.: defined on a sibling type
				return null;
			}
		}
		return entity;
	}

	@Override
	public PathType getSourceType()
	{
		return path.iterator().next().getSourceType();
	}

	@Override
	public PathType getTargetType()
	{
		return path.get(path.size() - 1).getTargetType();
	}

	@Override
	public Object getValue(Object entity)
	{
		for (int index = 0; index < path.size(); index++)
		{
			if (entity == null)
			{
				return null;
			}
			AttributePathElement attribute = path.get(index);
			if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
			{
				entity = attribute.getValue(entity);
			}
			else
			{
				// e.g.: defined on a sibling type
				return null;
			}
		}
		return entity;
	}

	@Override
	public int hashCode()
	{
		return path.hashCode();
	}

	@Override
	public boolean isContainingIndexesOrKeys()
	{
		for (AttributePathElement element : path)
		{
			if (element.isIndexOrKey())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWriteable()
	{
		return getAttribute().isWriteable();
	}

	@Override
	public void setValue(Object entity, Object value)
	{
		for (int index = 0; index < path.size() - 1; index++)
		{
			AttributePathElement attribute = path.get(index);
			if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
			{
				Object newEntity = attribute.getValue(entity);
				if (newEntity == null)
				{
					throw new NullPointerException("entity is null cannot set value " + this);
				}
				else
				{
					entity = newEntity;
				}
			}
			else
			{
				// e.g.: defined on a sibling type
				return;
			}

		}

		AttributePathElement attribute = path.get(path.size() - 1);
		if (attribute.getSourceType().getType().getJavaType().isAssignableFrom(entity.getClass()))
		{
			attribute.setValue(entity, value);
		}
		else
		{
			// e.g.: defined on a sibling type
			return;
		}

	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < path.size(); index++)
		{
			if (index > 0)
			{
				builder.append(".");
			}
			AttributePathElement pathElement = path.get(index);
			builder.append(pathElement.getName());
		}
		return builder.toString();
	}

}
