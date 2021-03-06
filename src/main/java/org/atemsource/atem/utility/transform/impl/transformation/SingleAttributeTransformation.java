/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.impl.common.attribute.SingleAssociationAttribute;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class SingleAttributeTransformation<A, B> extends
		AbstractOneToOneAttributeTransformation<A, B> {

	@Override
	protected void transformInternally(Object a, Object b,
			AttributePath attributeA, AttributePath attributeB,
			TransformationContext ctx,
			UniTransformation<Object, Object> converter) {
		if (attributeB.isWriteable()) {
			Object valueA = attributeA.getValue(a);

			if (converter != null && valueA != null) {
				Object valueB = attributeB.getValue(b);
				if (valueB == null) {
					 valueB = TransformUtils.findTargetEntity(converter,
							(A) valueA, (B) attributeB.getValue(b), ctx,
							attributeB.getAttribute(), b);
				}
				if (valueB == null) {
					valueA = converter.convert(valueA, ctx);
				} else {
					valueA = converter.merge(valueA, valueB, ctx);
				}
			}

			if (attributeB.getAttribute().isRequired() && valueA == null) {
				// TODO
			} else {
				attributeB.setValue(b, valueA);

			}
		}

	}
}
