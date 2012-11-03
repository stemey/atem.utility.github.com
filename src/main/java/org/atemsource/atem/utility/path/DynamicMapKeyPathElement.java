/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.path;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.EntityType;

public class DynamicMapKeyPathElement implements AttributePathElement {
	private String key;
	private String property;

	public DynamicMapKeyPathElement(String property, String key) {
		super();
		if (key == null) {
			throw new NullPointerException("key is null");
		}
		if (property == null) {
			throw new NullPointerException("property is null");
		}
		this.key = key;
		this.property = property;
	}

	public Attribute getAttribute() {
		return null;
	}

	@Override
	public String getName() {
		return property+"."+key;
	}

	@Override
	public PathType getSourceType() {
		return new PathType(Cardinality.SINGLE, null);
	}

	@Override
	public PathType getTargetType() {

		return new PathType(Cardinality.SINGLE, null);

	}

	@Override
	public Object getValue(Object entity) {
		EntityType<Object> entityType = BeanLocator.getInstance()
				.getInstance(EntityTypeRepository.class).getEntityType(entity);
		MapAttribute attribute = (MapAttribute) entityType.getAttribute(property);
		if (attribute == null) {
			return null;
		} else {
			return attribute.getElement(entity,key);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DynamicMapKeyPathElement other = (DynamicMapKeyPathElement) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	@Override
	public boolean isIndexOrKey() {
		return false;
	}

	@Override
	public void setValue(Object entity, Object value) {
		EntityType<Object> entityType = BeanLocator.getInstance()
				.getInstance(EntityTypeRepository.class).getEntityType(entity);
		MapAttribute attribute = (MapAttribute) entityType.getAttribute(property);
		if (attribute != null) {
			attribute.putElement(entity,key, value);
		}
	}
}
