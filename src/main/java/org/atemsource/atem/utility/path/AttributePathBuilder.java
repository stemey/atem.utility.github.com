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


import java.util.ArrayList;
import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.OrderableCollection;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.type.AttributeUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class AttributePathBuilder
{

	private List<AttributePathElement> newPath = new ArrayList<AttributePathElement>();

	public AttributePathBuilder addAttribute(Attribute<?, ?> attribute)
	{
		newPath.add(new AttributeAttributePathElement(attribute));
		return this;
	}

	public AttributePathBuilder addAttribute(String property)
	{

		Type<?> returnType = getTargetType();
		if (returnType instanceof EntityType)
		{
			EntityType entityType = (EntityType) returnType;
			Attribute attribute = AttributeUtils.findAttribute(property, entityType);
			if (attribute != null)
			{
				newPath.add(new AttributeAttributePathElement(attribute));
			}
			else
			{
				throw new IllegalStateException("evaluation time attribute not implemented yet" + newPath.toString());
			}
		}
		else
		{
			throw new IllegalStateException("no attributes possible" + newPath.toString());
		}

		return this;
	}

	public void addElement(String pathElement)
	{
		if (getAttribute() != null)
		{
			if (getAttribute() instanceof MapAttribute)
			{
				throw new UnsupportedOperationException(
					"converting from string based key to object based is notimplemented");
			}
			else if (getAttribute() instanceof OrderableCollection)
			{
				addIndex(Integer.parseInt("pathElement"));
			}
			else
			{
				addAttribute(pathElement);
			}
		}
	}

	public AttributePathBuilder addIndex(int index)
	{
		try
		{
			Attribute attribute = getAttribute();
			if (attribute == null)
			{
				throw new IllegalStateException("cannot handle index here" + newPath.toString());
			}
			newPath.add(new IndexPathElement(index, (OrderableCollection) attribute));
			return this;
		}
		catch (ClassCastException e)
		{
			throw new IllegalStateException("cannot add index here: " + newPath.toString());
		}
	}

	public AttributePathBuilder addMapKey(Object key)
	{
		try
		{
			Attribute attribute = getAttribute();
			newPath.add(new MapKeyPathElement(key, (MapAttribute) attribute));
			return this;
		}
		catch (ClassCastException e)
		{
			throw new IllegalStateException("cannot add index here: " + newPath.toString());
		}
	}

	public void addPath(AttributePath basePath)
	{
		newPath.add(new AttributePathAttributePathElement(basePath));
	}

	public AttributePath createPath()
	{
		return new AttributePathImpl(newPath);
	}

	private Attribute getAttribute()
	{
		AttributePathElement element = newPath.get(newPath.size() - 1);
		Attribute attribute = element.getAttribute();
		if (attribute == null)
		{
			return null;
		}
		else
		{
			return attribute;
		}
	}

	private Type<?> getTargetType()
	{
		AttributePathElement element = newPath.get(newPath.size() - 1);
		return element.getTargetType().getType();
	}

	public AttributePathBuilder start(String property, EntityType baseType)
	{
		addAttribute(baseType.getAttribute(property));
		return this;
	}

}
