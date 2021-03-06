/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.compare.AttributeComparison;
import org.atemsource.atem.utility.compare.CompareContext;
import org.atemsource.atem.utility.compare.Difference;


public class CollectionAttributeComparison extends AttributeComparison
{

	private IdentityCheck identityCheck;

	@Override
	public Set<Difference> getDifferences(CompareContext context, Object a, Object b)
	{
		Set<Difference> differences = new HashSet<Difference>();
		CompareContext childContext = context.createChild(getAttribute());
		Collection<Object> additions = new ArrayList<Object>();
		Collection<Object> removals = new ArrayList<Object>();
		CollectionAttribute associationAttribute = ((CollectionAttribute) getAttribute());
		Collection collectionA = associationAttribute.getElements(a);
		Collection collectionB = associationAttribute.getElements(b);

		if (collectionA == null && collectionB == null)
		{
			return Collections.emptySet();
		}
		else if (collectionA == null)
		{
			collectionA = Collections.emptyList();
		}
		else if (collectionB == null)
		{
			collectionB = Collections.emptyList();
		}

		Iterator ia = associationAttribute.getIterator(a);
		removals.addAll(collectionA);
		additions.addAll(collectionB);
		// TODO performance bad because of O(2) behavior.
		for (; ia.hasNext();)
		{
			final Object valueA = ia.next();
			Iterator ib = associationAttribute.getIterator(b);
			for (; ib.hasNext();)
			{
				final Object valueB = ib.next();
				Type targetType = getTargetType(associationAttribute, valueA);
				Type compareType = getTargetType(associationAttribute, valueB);
				if (getEntityOperation(compareType) != null && targetType.equals(compareType))
				{
					Set<Difference> localDifferences =
						getEntityOperation(compareType).getDifferences(childContext, valueA, valueB);
					if (localDifferences.size() == 0)
					{
						additions.remove(valueB);
						removals.remove(valueA);
						break;
					}
				}
				else if (targetType.isEqual(valueA, valueB))
				{
					additions.remove(valueB);
					removals.remove(valueA);
					break;
				}

			}
		}
		for (Object value : removals)
		{
			differences.add(childContext.addRemoval(value));
		}
		for (Object value : additions)
		{
			differences.add(childContext.addAddition(value));
		}
		return differences;
	}

	public IdentityCheck getIdentityCheck()
	{
		return identityCheck;
	}

	private Type getTargetType(CollectionAttribute associationAttribute, Object value)
	{
		Type targetType =
			value != null ? associationAttribute.getTargetType(value) : associationAttribute.getTargetType();
		if (targetType == null)
			targetType = associationAttribute.getTargetType();
		return targetType;
	}

	public void setIdentityCheck(IdentityCheck identityCheck)
	{
		this.identityCheck = identityCheck;
	}
}
