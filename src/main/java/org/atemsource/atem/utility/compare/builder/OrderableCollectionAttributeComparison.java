/*******************************************************************************
 * Stefan Meyer, 2012
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare.builder;


import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.atemsource.atem.api.attribute.OrderableCollection;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.compare.AttributeComparison;
import org.atemsource.atem.utility.compare.CompareContext;
import org.atemsource.atem.utility.compare.Difference;


public class OrderableCollectionAttributeComparison extends AttributeComparison
{

	private void geenrateMovals(Set<Difference> differences, Object a, Object b, CompareContext context,
		OrderableCollection attribute, CompareContext childContext, Set<Integer> movalsA, Set<Integer> movalsB)
	{
		Collection associatedEntityA = attribute.getElements(a);
		Collection associatedEntityB = attribute.getElements(b);
		Iterator ia = associatedEntityA.iterator();
		Iterator ib = associatedEntityB.iterator();
		for (int indexA = 0; indexA < attribute.getSize(a); indexA++)
		{
			Object valueA = attribute.getElement(a, indexA);
			for (int indexB = 0; indexB < attribute.getSize(b); indexB++)
			{
				if (indexA == indexB)
				{
					continue;
				}
				Object valueB = attribute.getElement(b, indexB);
				Type targetType = attribute.getTargetType(valueA);
				if (!targetType.equals(attribute.getTargetType(valueB)))
				{

				}
				else if (targetType.isEqual(valueA, valueB))
				{
					movalsA.add(indexA);
					movalsB.add(indexB);
					differences.add(childContext.addMotion(indexA, indexB, valueA));
					break;
				}
				else
				{
					Set<Difference> childDifferences = getEntityOperation().getDifferences(childContext, valueA, valueB);
					if (childDifferences.size() == 0)
					{
						movalsA.add(indexA);
						movalsB.add(indexB);
						differences.add(childContext.addMotion(indexA, indexB, valueA));
						break;
					}
				}

			}
		}
	}

	@Override
	public Set<Difference> getDifferences(CompareContext context, Object a, Object b)
	{
		OrderableCollection<Object, Object> attribute = (OrderableCollection<Object, Object>) getAttribute();
		Set<Difference> differences = new HashSet<Difference>();
		CompareContext childContext = context.createChild(attribute);

		Set<Integer> movalsA = new HashSet<Integer>();
		Set<Integer> movalsB = new HashSet<Integer>();
		geenrateMovals(differences, a, b, context, attribute, childContext, movalsA, movalsB);

		int sizeA = attribute.getSize(a);
		int sizeB = attribute.getSize(b);
		int maxSize = (sizeA > sizeB) ? sizeA : sizeB;

		for (int index = 0; index < maxSize; index++)
		{
			CompareContext childItemContext = childContext.createIndexedChild(index);
			Object valueA = null;
			if (index < sizeA)
			{
				valueA = attribute.getElement(a, index);
			}
			Object valueB = null;
			if (index < sizeB)
			{
				valueB = attribute.getElement(b, index);
			}
			if (movalsA.contains(index) && movalsB.contains(index))
			{
				continue;
			}
			else if (movalsA.contains(index))
			{
				differences.add(childItemContext.addAddition(valueB));
			}
			else if (movalsB.contains(index))
			{
				differences.add(childItemContext.addRemoval(valueA));
			}
			else
			{
				if (valueA == null && valueB != null)
				{
					differences.add(childItemContext.addAddition(valueB));
				}
				else if (valueB == null && valueA != null)
				{
					differences.add(childItemContext.addRemoval(valueA));
				}
				else if (valueB == null && valueA == null)
				{

				}
				else
				{
					Type targetTypeA = attribute.getTargetType(valueA);
					Type targetTypeB = attribute.getTargetType(valueB);
					if (!attribute.getTargetType(valueA).equals(attribute.getTargetType(valueB)))
					{
						differences.add(childItemContext.addAttributeChange(valueA, valueB));
					}
					else if (getEntityOperation() == null)
					{
						if (!targetTypeA.isEqual(valueA, valueB))
						{
							differences.add(childItemContext.addAttributeChange(valueA, valueB));
						}
					}
					else
					{
						differences.addAll(getEntityOperation().getDifferences(childItemContext, valueA, valueB));
					}
				}
			}
		}
		return differences;
	}
}
