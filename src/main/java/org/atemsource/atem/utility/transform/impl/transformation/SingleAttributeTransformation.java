/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class SingleAttributeTransformation<A, B> extends OneToOneAttributeTransformation<A, B>
{

	@Override
	protected void transformInternally(Object a, Object b, AttributePath attributeA, AttributePath attributeB,
		TransformationContext ctx, UniTransformation<Object, Object> converter)
	{
		Attribute attribute=attributeB.getAttribute();
		if (attribute.isWriteable())
		{
			Object valueA = attributeA.getAttribute().getValue(a);
			if (valueA == null)
			{
				return;
			}

			if (converter != null)
			{
				valueA = converter.convert(valueA, ctx);
			}
			if (attribute.isRequired() && valueA == null)
			{

				// TODO
			}
			else
			{
				attribute.setValue(b, valueA);

			}
		}

	}
}
