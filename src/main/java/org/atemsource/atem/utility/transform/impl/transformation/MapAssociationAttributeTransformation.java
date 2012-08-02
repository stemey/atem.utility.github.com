/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class MapAssociationAttributeTransformation<A, B> extends
		AbstractAttributeTransformation<A, B> {

	@Override
	public void mergeBA(B b, A a, TransformationContext ctx) {
		UniConverter baConverter = getConverter() == null ? null
				: getConverter().getBA();
		UniConverter<Object, Object> baKeyConverter = getKeyConverter() == null ? null
				: keyConverter.getBA();
		MapAssociationAttributeTransformation.this.transformInternally(b, a,
				getAttributeB(), getAttributeA(), ctx, baConverter,
				baKeyConverter);
	}

	@Override
	public void mergeAB(A a, B b, TransformationContext ctx) {
		UniConverter abConverter = getConverter() == null ? null
				: getConverter().getAB();
		UniConverter<Object, Object> abKeyConverter = getKeyConverter() == null ? null
				: keyConverter.getAB();
		MapAssociationAttributeTransformation.this.transformInternally(a, b,
				getAttributeA(), getAttributeB(), ctx, abConverter,
				abKeyConverter);
	}

	private boolean convertNullToEmpty;

	private Converter<Object, Object> keyConverter;

	private Type keyType;

	public Converter getKeyConverter() {
		return keyConverter;
	}

	public Type getKeyType() {
		return keyType;
	}

	public boolean isConvertNullToEmpty() {
		return convertNullToEmpty;
	}

	public void setConvertNullToEmpty(boolean convertNullToEmpty) {
		this.convertNullToEmpty = convertNullToEmpty;
	}

	public void setKeyConverter(Converter keyConverter) {
		this.keyConverter = keyConverter;
	}

	public void setKeyType(Type keyType) {
		this.keyType = keyType;
	}

	@Override
	protected void transformInternally(Object a, Object b,
			AttributePath attributeA, AttributePath attributeB,
			TransformationContext ctx, UniConverter<Object, Object> ab) {

	}

	protected void transformInternally(Object a, Object b,
			AttributePath attributePathA, AttributePath attributePathB,
			TransformationContext ctx, UniConverter<Object, Object> converter,
			UniConverter<Object, Object> keyConverter) {
		MapAttribute<Object, Object, Object> attributeA = (MapAttribute<Object, Object, Object>) attributePathA
				.getAttribute(a);
		MapAttribute<Object, Object, Object> attributeB = (MapAttribute<Object, Object, Object>) attributePathB
				.getAttribute(b);
		Object baseValueU = attributePathA.getBaseValue(a);

		Object valueB = attributeB.getValue(b);
		if (valueB == null) {
			Object emptyCollection = attributeB.getEmptyMap();

			attributeB.setValue(b, emptyCollection);
		}
		if (attributeA.getValue(a) != null && attributeA.getSize(a) > 0) {

			// attributeV.setValue(v, emptyCollection);
			for (Iterator<Map.Entry<?, ?>> iterator = attributeA.getIterator(a); iterator
					.hasNext();) {
				Entry<?, ?> next = iterator.next();
				Object elementA = next.getValue();

				Object keyB = next.getKey();
				if (keyConverter != null) {
					keyB = keyConverter.convert(next.getKey(), ctx);

				}
				Object elementB = attributeB.getElement(b, keyB);
				if (elementB == null) {
					if (converter != null) {
						elementB = converter.convert(elementA, ctx);
					} else {
						elementB = elementA;
					}
					attributeB.putElement(b, keyB, elementB);
				}
			}
		} else {
			if (convertNullToEmpty) {
				attributeB.setValue(b, null);
			} else {
				Object emptyCollection = attributeB.getEmptyMap();
				attributeB.setValue(b, emptyCollection);
			}

		}
	}
}
