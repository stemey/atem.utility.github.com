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


import java.util.HashSet;
import java.util.Set;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.compare.AttributeComparison;
import org.atemsource.atem.utility.compare.CompareContext;
import org.atemsource.atem.utility.compare.Difference;


public class SingleAttributeComparison extends AttributeComparison
{

	@Override
	public Set<Difference> getDifferences(CompareContext context, Object a, Object b)
	{
		Set<Difference> differences = new HashSet<Difference>();
		CompareContext childContext = context.createChild(getAttribute());
		Object a2 = getAttribute().getValue(a);
		Object b2 = getAttribute().getValue(b);

		Type targetType = getAttribute().getTargetType(a2);
		if (a2 == null && b2 == null)
		{
		}
		else if (a2 == null && b2 != null || a2 != null && b2 == null)
		{
			differences.add(childContext.addAttributeChange(a2, b2));
		}
		else if (!targetType.equals(getAttribute().getTargetType(b2)))
		{
			// types are not the same
			differences.add(childContext.addAttributeChange(a2, b2));
		}
		else if (getEntityOperation(targetType) != null)
		{
			differences.addAll(getEntityOperation(targetType).getDifferences(childContext, a2, b2));
		}
		else if (!targetType.isEqual(a2, b2))
		{
			differences.add(childContext.addAttributeChange(a2, b2));
		}

		return differences;
	}

}
