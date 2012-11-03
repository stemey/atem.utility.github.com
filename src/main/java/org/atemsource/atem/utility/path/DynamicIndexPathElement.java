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
import org.atemsource.atem.api.attribute.OrderableCollection;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.common.attribute.collection.ListAttributeImpl;

public class DynamicIndexPathElement implements AttributePathElement {
	private int index;
	private String property;

	public DynamicIndexPathElement(String property, int index) {
		super();
		if (property == null) {
			throw new NullPointerException("property is null");
		}
		this.index=index;
		this.property = property;
	}

	public Attribute getAttribute() {
		return null;
	}

	@Override
	public String getName() {
		return property+"."+index;
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
		OrderableCollection attribute = (OrderableCollection) entityType.getAttribute(property);
		if (attribute == null) {
			return null;
		} else {
			return attribute.getElement(entity,index);
		}
	}



	@Override
	public boolean isIndexOrKey() {
		return true;
	}

	@Override
	public void setValue(Object entity, Object value) {
		EntityType<Object> entityType = BeanLocator.getInstance()
				.getInstance(EntityTypeRepository.class).getEntityType(entity);
		OrderableCollection attribute = (OrderableCollection) entityType.getAttribute(property);
		if (attribute != null) {
			attribute.addElement(entity,index, value);
		}
	}
}
