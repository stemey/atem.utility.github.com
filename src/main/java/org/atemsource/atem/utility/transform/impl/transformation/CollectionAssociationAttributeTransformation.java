/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.Collection;
import java.util.Iterator;

import org.atemsource.atem.api.attribute.AssociationAttribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Filter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CollectionAssociationAttributeTransformation<A, B> extends
		AbstractOneToOneAttributeTransformation<A, B> {
	private Class<?> associationType;

	private boolean convertEmptyToNull;

	private boolean convertNullToEmpty;

	private Filter<Object> filter;

	public boolean isConvertEmptyToNull() {
		return convertEmptyToNull;
	}

	public boolean isConvertNullToEmpty() {
		return convertNullToEmpty;
	}

	public void setAssociationType(Class<?> associationType) {
		this.associationType = associationType;
	}

	public void setConvertEmptyToNull(boolean convertEmptyToNull) {
		this.convertEmptyToNull = convertEmptyToNull;
	}

	public void setConvertNullToEmpty(boolean convertNullToEmpty) {
		this.convertNullToEmpty = convertNullToEmpty;
	}

	public void setFilter(Filter<Object> filter) {
		this.filter = filter;
	}

	@Override
	protected void transformInternally(Object a, Object b,
			AttributePath attributePathA, AttributePath attributePathB,
			TransformationContext ctx,
			UniTransformation<Object, Object> converter) {
		CollectionAttribute<Object, Object> attributeA = (CollectionAttribute<Object, Object>) attributePathA
				.getAttribute(a);
		if (attributeA == null) {
			// TODO above seems to be null sometimes
			attributeA = (CollectionAttribute<Object, Object>) attributePathA
					.getAttribute();
		}
		CollectionAttribute<Object, Object> attributeB = (CollectionAttribute<Object, Object>) attributePathB
				.getAttribute();
		Object baseValueA = attributePathA.getBaseValue(a);

		if (baseValueA == null) {
			return;
		}

		if (associationType != null) {
			try {
				attributeB.setValue(b, associationType.newInstance());
			} catch (InstantiationException e) {
				throw new TechnicalException("cannot instantiate collection", e);
			} catch (IllegalAccessException e) {
				throw new TechnicalException("cannot instantiate collection", e);
			}

		} else {
			attributeB.clear(b);
		}

		Object associatedEntities = attributeA.getValue(baseValueA);
		int size = attributeA.getSize(baseValueA);
		if (convertEmptyToNull && size == 0) {
			attributeB.setValue(b, null);
			return;
		}

		// Object emptyCollection;
		// TODO associationType needs to be saved in EntityType and used in
		// attribute.clear() or attribute.add()
		attributeB.clear(b);

		if (associatedEntities != null) {
			Iterator<Object> iterator = attributeA.getIterator(a);
			for (; iterator.hasNext();) {
				Object valueA = iterator.next();
				if (filter == null || filter.isInluded(valueA)) {
					Object valueB = null;
					if (converter != null) {
						//TODO we need to find the 
						if ( attributeB instanceof AssociationAttribute && attributeB.isComposition()) {
						valueB = ((AssociationAttribute) attributeB)
								.createTarget(getTypeB(), b);
						converter.merge(valueA, valueB, ctx);
						}
						if (valueB==null) {
							valueB = converter.convert(valueA, ctx);
						}
					} else {
						valueB = valueA;
					}
					attributeB.addElement(b, valueB);
				}
			}
		}
	}
}
