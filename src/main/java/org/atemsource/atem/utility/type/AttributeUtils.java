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
package org.atemsource.atem.utility.type;


import java.util.Set;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;


public class AttributeUtils
{

	public static Attribute<?, ?> findAttribute(String attributeCode, EntityType<?> type)
	{
		Attribute attribute = type.getAttribute(attributeCode);
		if (attribute == null)
		{
			attribute = findAttributeInSubType(attributeCode, type.getSubEntityTypes(true));
		}
		return attribute;
	}

	private static Attribute findAttributeInSubType(String attributeCode, Set<EntityType> subEntityTypes)
	{
		if (subEntityTypes == null)
		{
			return null;
		}
		for (EntityType type : subEntityTypes)
		{
			Attribute attribute = type.getAttribute(attributeCode);
			if (attribute != null)
			{
				return attribute;
			}
			else
			{
				attribute = findAttributeInSubType(attributeCode, type.getSubEntityTypes(true));
				if (attribute != null)
				{
					return attribute;
				}
			}
		}
		return null;
	}

	
}
