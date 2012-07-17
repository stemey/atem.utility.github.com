/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare.builder;

import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.api.attribute.MapAttribute;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.compare.AttributeComparison;
import org.atemsource.atem.utility.compare.CompareContext;
import org.atemsource.atem.utility.compare.Difference;


public class MapAttributeComparison extends AttributeComparison
{

	@Override
	public Set<Difference> getDifferences(CompareContext context, Object a, Object b)
	{
		Set<Difference> differences = new HashSet<Difference>();
		MapAttribute<Object, Object, Object> mapAttribute = (MapAttribute<Object, Object, Object>) getAttribute();
		CompareContext childContext = context.createChild(mapAttribute);
		Object mapA = mapAttribute.getValue(a);
		Object mapB = mapAttribute.getValue(b);

		Set<Object> keysA = mapAttribute.getKeySet(a);
		Set<Object> keysB = mapAttribute.getKeySet(b);
		Set<Object> allKeys = new HashSet<Object>();
		allKeys.addAll(keysA);
		allKeys.addAll(keysB);
		for (Object key : allKeys)
		{
			CompareContext childItemContext = childContext.createMapChild(key);
			Object valueA = mapAttribute.getElement(a, key);
			Object valueB = mapAttribute.getElement(b, key);
			if (valueA == null && valueB == null)
			{

			}
			else if (valueB == null)
			{
				differences.add(childItemContext.addRemoval(valueA));
			}
			else if (valueA == null)
			{
				differences.add(childItemContext.addAddition(valueB));
			}
			else
			{

				Type targetType = mapAttribute.getTargetType(valueA);
				if (!targetType.equals(mapAttribute.getTargetType(valueB)))
				{
					differences.add(childItemContext.addAttributeChange(valueA, valueB));
				}
				else if (getEntityOperation(targetType) == null)
				{
					if (!targetType.isEqual(valueA, valueB))
					{
						differences.add(childItemContext.addAttributeChange(valueA, valueB));
					}
				}
				else
				{
					differences.addAll(getEntityOperation(targetType).getDifferences(childItemContext, valueA, valueB));
				}
			}

		}
		return differences;
	}

}
