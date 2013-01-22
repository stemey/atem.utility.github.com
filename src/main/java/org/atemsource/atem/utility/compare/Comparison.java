/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.compare;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.utility.common.EntityOperation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class Comparison extends EntityOperation<AttributeComparison, Comparison>
{

	@Inject
	private BeanLocator beanLocator;

	public Set<Difference> getDifferences(CompareContext context, Object oldValue, Object newValue)
	{
		Set<Difference> differences = new HashSet<Difference>();
		for (AttributeComparison attributeComparison : getAttributeOperations())
		{
			differences.addAll(attributeComparison.getDifferences(context, oldValue, newValue));
		}
		return differences;
	}

	public Set<Difference> getDifferences(Object oldValue, Object newValue)
	{
		CompareContext context = beanLocator.getInstance(CompareContext.class);
		Set<Difference> differences = new HashSet<Difference>();
		for (AttributeComparison attributeComparison : getAttributeOperations())
		{
			differences.addAll(attributeComparison.getDifferences(context, oldValue, newValue));
		}
		return differences;
	}

}
