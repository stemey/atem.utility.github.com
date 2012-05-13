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
package org.atemsource.atem.utility.transform.impl;


import java.util.List;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.BeanLocator;


public class EntityUtils
{

	public static <T, S extends T, U extends T> void merge(S from, U to, Class<T> clazz)
	{
		EntityType<T> entityType = BeanLocator.getInstance().getInstance(EntityTypeRepository.class).getEntityType(clazz);
		merge(from, to, entityType);

	}

	public static <T, S extends T, U extends T> void merge(S from, U to, EntityType<T> entityType)
	{
		do
		{
			List<Attribute> attributes = entityType.getAttributes();
			for (Attribute attribute : attributes)
			{
				if (attribute.isWriteable())
				{

					Object value = attribute.getValue(from);
					attribute.setValue(to, value);

				}
			}
			entityType = entityType.getSuperEntityType();
		}
		while (entityType != null);

	}

}
