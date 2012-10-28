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

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.common.attribute.AbstractAttribute;
import org.atemsource.atem.impl.common.attribute.MapAttributeImpl;
import org.atemsource.atem.impl.common.attribute.PrimitiveAttributeImpl;
import org.atemsource.atem.impl.common.attribute.SingleAssociationAttribute;
import org.atemsource.atem.impl.common.attribute.SingleAttributeImpl;
import org.atemsource.atem.impl.common.attribute.collection.ListAttributeImpl;
import org.atemsource.atem.impl.common.attribute.collection.SetAttributeImpl;

public class AttributePathImpl implements AttributePath {

	private final List<AttributePathElement> path;
	private BeanLocator beanLocator;
	private Attribute attribute;

	public AttributePathImpl() {
		super();
		this.path = new ArrayList();
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public Attribute createPath() {
		AttributePathElement attributePathElement = path.get(path.size() - 1);
		AttributePathElement firstElement = path.get(0);
		if (attributePathElement instanceof IndexPathElement) {
			AttributePathElement secondLastAttributePathElement = path.get(path
					.size() - 2);
			Attribute attribute = secondLastAttributePathElement.getAttribute();
			AbstractAttribute pathAttribute = createSingleAttribute(attribute.getTargetType());
			pathAttribute.setEntityType(firstElement.getAttribute()
					.getEntityType());
			pathAttribute.setTargetType(attribute.getTargetType());
			pathAttribute.setAccessor(new PathAccessor(path));
			return pathAttribute;
		} else if (attributePathElement instanceof MapKeyPathElement) {
			AttributePathElement secondLastAttributePathElement = path.get(path
					.size() - 2);
			Attribute attribute = secondLastAttributePathElement.getAttribute();
			AbstractAttribute pathAttribute = createSingleAttribute(attribute.getTargetType());
			pathAttribute.setEntityType(firstElement.getAttribute()
					.getEntityType());
			pathAttribute.setTargetType(attribute.getTargetType());
			pathAttribute.setAccessor(new PathAccessor(path));
			return pathAttribute;
		} else {
			Attribute attribute = attributePathElement.getAttribute();
			AbstractAttribute pathAttribute = createAttribute(attribute);
			pathAttribute.setEntityType((EntityType) firstElement.getSourceType().getType());
			pathAttribute.setTargetType(attribute.getTargetType());
			pathAttribute.setAccessor(new PathAccessor(path));
			return pathAttribute;
		}
	}

	private AbstractAttribute createAttribute(Attribute attribute) {
		if (attribute instanceof SingleAttribute) {
			return createSingleAttribute(attribute.getTargetType());
		} else if (attribute instanceof MapAttribute) {
			return createMapAttribute(((MapAttribute)attribute).getKeyType());
		} else {
			return createCollectionAttribute(((CollectionAttribute) attribute)
					.getCollectionSortType());
		}
	}

	private AbstractAttribute createCollectionAttribute(
			CollectionSortType collectionSortType) {
		switch (collectionSortType) {
		case NONE:
			return beanLocator.getInstance(SetAttributeImpl.class);
		case ORDERABLE:
			return beanLocator.getInstance(ListAttributeImpl.class);
		case SORTED:
			return beanLocator.getInstance(SetAttributeImpl.class);
		default:
			throw new IllegalStateException("unknown collectionsorttype "
					+ collectionSortType);
		}

	}

	private AbstractAttribute createMapAttribute(Type keyType) {
		MapAttributeImpl mapAttribute = beanLocator
				.getInstance(MapAttributeImpl.class);
		mapAttribute.setKeyType(keyType);
		return mapAttribute;
	}

	private AbstractAttribute createSingleAttribute(Type targetType) {
		if (targetType instanceof EntityType) {
		SingleAttributeImpl singleAttribute = beanLocator
				.getInstance(SingleAssociationAttribute.class);
		return singleAttribute;
		}else{
			return new PrimitiveAttributeImpl();
		}
	}

	public AttributePathImpl(Attribute newAttribute) {
		path = new ArrayList<AttributePathElement>();
		path.add(new AttributeAttributePathElement(newAttribute));
	}

	AttributePathImpl(List<AttributePathElement> path, BeanLocator beanLocator) {
		super();
		this.path = path;
		this.beanLocator = beanLocator;
		this.attribute = createPath();
	}

	@Override
	public String getAsString() {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < path.size(); index++) {
			if (index > 0) {
				builder.append(".");
			}
			AttributePathElement pathElement = path.get(index);
			builder.append(pathElement.getName());
		}
		return builder.toString();
	}

	public Attribute getAttribute(Object entity) {
		if (entity == null) {
			return null;
		}
		AttributePathElement attributePathElement = path.get(path.size() - 1);
		AttributePathElement firstElement = path.get(0);
		if (attributePathElement instanceof IndexPathElement) {
			return null;
		} else if (attributePathElement instanceof MapKeyPathElement) {
			return null;

		} else {
			Attribute attribute = attributePathElement.getAttribute();
			AbstractAttribute pathAttribute = createAttribute(attribute);
			pathAttribute.setEntityType(firstElement.getAttribute()
					.getEntityType());
			pathAttribute.setTargetType(attribute.getTargetType(entity));
			pathAttribute.setAccessor(new PathAccessor(path));
			return pathAttribute;
		}
	}

	public Object getBaseValue(Object entity) {
		for (int index = 0; index < path.size() - 1; index++) {
			if (entity == null) {
				return null;
			}
			AttributePathElement attribute = path.get(index);
			if (attribute.getSourceType().getType().getJavaType()
					.isAssignableFrom(entity.getClass())) {
				entity = attribute.getValue(entity);
			} else {
				// e.g.: defined on a sibling type
				return null;
			}
		}
		return entity;
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < path.size(); index++) {
			if (index > 0) {
				builder.append(".");
			}
			AttributePathElement pathElement = path.get(index);
			builder.append(pathElement.getName());
		}
		return builder.toString();
	}

}
