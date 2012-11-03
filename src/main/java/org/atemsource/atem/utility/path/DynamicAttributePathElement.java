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

public class DynamicAttributePathElement implements AttributePathElement {
	private String property;

	public DynamicAttributePathElement(String property) {
		super();
		if (property == null) {
			throw new NullPointerException("property is null");
		}
		this.property = property;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DynamicAttributePathElement other = (DynamicAttributePathElement) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	public Attribute getAttribute() {
		return null;
	}

	@Override
	public String getName() {
		return property;
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
		Attribute attribute = entityType.getAttribute(property);
		if (attribute == null) {
			return null;
		} else {
			return attribute.getValue(entity);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean isIndexOrKey() {
		return false;
	}

	@Override
	public void setValue(Object entity, Object value) {
		EntityType<Object> entityType = BeanLocator.getInstance()
				.getInstance(EntityTypeRepository.class).getEntityType(entity);
		Attribute attribute = entityType.getAttribute(property);
		if (attribute != null) {
			 attribute.setValue(entity,value);
		}
	}
}
