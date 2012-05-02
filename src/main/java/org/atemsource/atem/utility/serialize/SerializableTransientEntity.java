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
package org.atemsource.atem.utility.serialize;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.atemsource.atem.api.attribute.Attribute;


public class SerializableTransientEntity extends SerializableEntity
{

	private Map<String, Serializable> values = new HashMap<String, Serializable>();

	@Override
	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof SerializableTransientEntity)
		{
			SerializableTransientEntity other = (SerializableTransientEntity) obj;
			if (other.getTypedId().getTypeCode().equals(getTypedId().getTypeCode()))
			{
				for (String attributeCode : values.keySet())
				{
					Object otherValue = other.values.get(attributeCode);
					Object thisValue = values.get(attributeCode);
					if (thisValue == null)
					{
						if (otherValue != null)
						{
							return false;
						}
					}
					else
					{
						if (otherValue == null)
						{
							return false;
						}
						else
						{
							if (!thisValue.equals(otherValue))
							{
								return false;
							}
						}
					}
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public Serializable getAttribute(Attribute attribute)
	{
		return values.get(attribute.getCode());
	}

	@Override
	public int hashCode()
	{
		return values.hashCode();
	}

	public void setAttribute(Attribute attribute, Serializable value)
	{
		values.put(attribute.getCode(), value);
	}

}
