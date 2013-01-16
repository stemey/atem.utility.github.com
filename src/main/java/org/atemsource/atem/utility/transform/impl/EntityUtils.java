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

public class EntityUtils {

/**
* Copy the attributes of clazz from to to. 
* @param from the object to copy from
* @param to the object to copy attribute values to
* @param commonSuperClass the common super class of from and to
*/
	public static <T, S extends T, U extends T> void merge(S from, U to,
			Class<T> commonSuperClass) {
		EntityType<T> entityType = BeanLocator.getInstance()
				.getInstance(EntityTypeRepository.class).getEntityType(commonnSuperClass);
		merge(from, to, entityType);

	}

/**
* Copy the common attributes of from to to.
* @param from the object to copy from
* @param to the object to copy attribute values to
* @param entityType the common super type of from and to
*/
	public static <T, S extends T, U extends T> void merge(S from, U to,
			EntityType<T> entityType) {
		List<Attribute> attributes = entityType.getAttributes();
		for (Attribute attribute : attributes) {
			if (attribute.isWriteable()) {

				Object value = attribute.getValue(from);
				attribute.setValue(to, value);

			}
		}

	}

/**
* Copy the common attributes of from to to and do this recursively.
* @param from the object to copy from
* @param to the object to copy attribute values to
* @param commonSuperClass the common super class of from and to
*/
	public static <T, S extends T, U extends T> void mergeRecursive(S from,
			U to, EntityType<T> entityType) {
		do {
			merge(from, to, entityType);
			entityType = entityType.getSuperEntityType();
		} while (entityType != null);

	}

/**
* find the common super type of two Types.
*/
	public static<T> EntityType<T> getCommonAncestor(EntityType<?> a,
			EntityType<?> b) {
		while (a != null && b != null && !a.equals(b)) {
			if (a.isAssignableFrom(b)) {
				a = a.getSuperEntityType();
			} else if (b.isAssignableFrom(a)) {
				b = b.getSuperEntityType();
			} else {
				a = a.getSuperEntityType();
				b = b.getSuperEntityType();
			}
		}
		if (a == null || b == null) {
			return null;
		} else {
			return (EntityType<T>) a;
		}
	}

}
