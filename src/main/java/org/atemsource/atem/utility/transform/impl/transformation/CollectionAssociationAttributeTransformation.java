/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.Collection;

import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class CollectionAssociationAttributeTransformation<A, B> extends OneToOneAttributeTransformation<A, B>
{

	private boolean convertNullToEmpty;

	public boolean isConvertNullToEmpty()
	{
		return convertNullToEmpty;
	}

	public void setConvertNullToEmpty(boolean convertNullToEmpty)
	{
		this.convertNullToEmpty = convertNullToEmpty;
	}

	@Override
	protected void transformInternally(Object a, Object b, AttributePath attributePathA, AttributePath attributePathB,
		TransformationContext ctx, UniTransformation<Object, Object> converter)
	{
		CollectionAttribute<Object, Object> attributeA =
			(CollectionAttribute<Object, Object>) attributePathA.getAttribute(a);
		CollectionAttribute<Object, Object> attributeB =
			(CollectionAttribute<Object, Object>) attributePathB.getAttribute(b);
		Object baseValueA = attributePathA.getBaseValue(a);

		Collection<Object> associatedEntities = attributeA.getElements(baseValueA);

		Object emptyCollection = attributeB.getEmptyCollection(b);
		if (associatedEntities != null)
		{
			attributeB.setValue(b, emptyCollection);
			for (Object valueA : associatedEntities)
			{
				Object valueB;
				if (converter != null)
				{
					valueB = converter.convert(valueA, ctx);
				}
				else
				{
					valueB = valueA;
				}
				attributeB.addElement(b, valueB);
			}
		}
		else
		{
			if (convertNullToEmpty)
			{
				attributeB.setValue(b, null);
			}
			else
			{
				attributeB.setValue(b, emptyCollection);
			}

		}
	}

}
