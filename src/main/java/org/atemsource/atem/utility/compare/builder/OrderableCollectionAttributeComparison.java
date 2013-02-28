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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.atemsource.atem.api.attribute.OrderableCollection;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.compare.AttributeComparison;
import org.atemsource.atem.utility.compare.CompareContext;
import org.atemsource.atem.utility.compare.Difference;

/**
 * this comparison works in the following way:
 * 1. find elements in both collection that are equal. These are considered rearrangements. 
 * 2. go through the elements of both collections in one iteration. 
 * 2.1. If both elements are part of rearrangements continue
 * 2.2. If one of the elements is part of a rearrangement then the other one is considered to be removed or added. 
 * 2.3. If none is part of a rearrangement and they have the same type the element is considered changed.
 * 2.4. If none is part of a rearrangement and they don't have the same type the old element is considered to be removed and a new one added.
 * 3. All rearrangements that are caused by additions or removals are removed from the list of changes.
 *
 */
public class OrderableCollectionAttributeComparison extends AttributeComparison {

	private static class PreliminaryRearrangement {
		private int indexA;
		private int indexB;
		private Object value;

		public Object getValue() {
			return value;
		}

		public int getIndexA() {
			return indexA;
		}

		public int getIndexB() {
			return indexB;
		}

		public PreliminaryRearrangement(int indexA, int indexB, Object value) {
			super();
			this.indexA = indexA;
			this.indexB = indexB;
			this.value = value;
		}

	}

	/**
	 * go#es over both collections and finds equal elements. These are considered rearranged.
	 * @param a
	 * @param b
	 * @param context
	 * @param attribute
	 * @param childContext
	 * @param movalsA
	 * @param movalsB
	 * @param rearrangements
	 */
	private void findRearrangements(Object a, Object b, CompareContext context, OrderableCollection attribute,
			CompareContext childContext, Set<Integer> movalsA, Set<Integer> movalsB,
			List<PreliminaryRearrangement> rearrangements) {
		Collection associatedEntityA = attribute.getElements(a);
		Collection associatedEntityB = attribute.getElements(b);
		Iterator ia = associatedEntityA.iterator();
		Iterator ib = associatedEntityB.iterator();
		for (int indexA = 0; indexA < attribute.getSize(a); indexA++) {
			Object valueA = attribute.getElement(a, indexA);
			for (int indexB = 0; indexB < attribute.getSize(b); indexB++) {
				if (indexA == indexB) {
					continue;
				}
				Object valueB = attribute.getElement(b, indexB);
				Type targetType = attribute.getTargetType(valueA);
				if (!targetType.equals(attribute.getTargetType(valueB))) {

				} else if (getEntityOperation(targetType) != null) {
					Set<Difference> childDifferences = getEntityOperation(targetType).getDifferences(childContext,
							valueA, valueB);
					if (childDifferences.size() == 0) {
						movalsA.add(indexA);
						movalsB.add(indexB);
						rearrangements.add(new PreliminaryRearrangement(indexA, indexB, valueA));
						break;
					}
				} else if (targetType.isEqual(valueA, valueB)) {
					movalsA.add(indexA);
					movalsB.add(indexB);
					rearrangements.add(new PreliminaryRearrangement(indexA, indexB, valueA));
					break;
				}

			}
		}
	}

	@Override
	public Set<Difference> getDifferences(CompareContext context, Object a, Object b) {
		OrderableCollection<Object, Object> attribute = (OrderableCollection<Object, Object>) getAttribute();
		Set<Difference> differences = new HashSet<Difference>();
		CompareContext childContext = context.createChild(attribute);

		Set<Integer> movedIndicesA = new HashSet<Integer>();
		Set<Integer> movedIndicesB = new HashSet<Integer>();
		List<PreliminaryRearrangement> rearrangements = new LinkedList<PreliminaryRearrangement>();
		
		findRearrangements(a, b, context, attribute, childContext, movedIndicesA, movedIndicesB, rearrangements);

		int sizeA = attribute.getSize(a);
		int sizeB = attribute.getSize(b);
		int maxSize = (sizeA > sizeB) ? sizeA : sizeB;

		List<Integer> removalCountByIndexA = new ArrayList<Integer>();
		List<Integer> additionCountByIndexB = new ArrayList<Integer>();
		int removalCountA = 0;
		int additionCountB = 0;

		for (int index = 0; index < maxSize; index++) {
			CompareContext childItemContext = childContext.createIndexedChild(index);
			Object valueA = null;
			if (index < sizeA) {
				valueA = attribute.getElement(a, index);
			}
			Object valueB = null;
			if (index < sizeB) {
				valueB = attribute.getElement(b, index);
			}
			if (movedIndicesA.contains(index) && movedIndicesB.contains(index)) {
				// just two rearrangements
			} else if (movedIndicesA.contains(index)) {
				// the element on this index was moved away. we considered the element a new element.
				if (sizeB > index) {
					differences.add(childItemContext.addAddition(valueB));
					additionCountB++;
				}
			} else if (movedIndicesB.contains(index)) {
				// the element on this index was moved here. we considered the original element removed.
				if (sizeA > index) {
					differences.add(childItemContext.addRemoval(valueA));
					removalCountA++;
				}
			} else {
				if (valueA == null && valueB != null) {
					differences.add(childItemContext.addAddition(valueB));
					additionCountB++;
				} else if (valueB == null && valueA != null) {
					differences.add(childItemContext.addRemoval(valueA));
					removalCountA++;
				} else if (valueB == null && valueA == null) {

				} else {
					Type targetTypeA = attribute.getTargetType(valueA);
					Type targetTypeB = attribute.getTargetType(valueB);
					if (!attribute.getTargetType(valueA).equals(attribute.getTargetType(valueB))) {
						differences.add(childItemContext.addAttributeChange(valueA, valueB));
					} else if (getEntityOperation(targetTypeA) == null) {
						if (!targetTypeA.isEqual(valueA, valueB)) {
							differences.add(childItemContext.addAttributeChange(valueA, valueB));
						}
					} else {
						differences.addAll(getEntityOperation(targetTypeA).getDifferences(childItemContext, valueA,
								valueB));
					}
				}
			}
			removalCountByIndexA.add(removalCountA);
			additionCountByIndexB.add(additionCountB);
		}
		// remove the rearragmenets that are caused by additions and removals
		for (PreliminaryRearrangement arr : rearrangements) {
			int renomrmalizedIndexA = arr.getIndexA() - removalCountByIndexA.get(arr.getIndexA());
			int renomrmalizedIndexB = arr.getIndexB() - additionCountByIndexB.get(arr.getIndexB());
			if (renomrmalizedIndexB != renomrmalizedIndexA) {
				differences.add(childContext.addMotion(arr.getIndexA(), arr.getIndexB(), arr.getValue()));
			}
		}
		return differences;
	}
}
