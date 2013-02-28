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

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.common.EntityOperationSelector;
import org.atemsource.atem.utility.compare.AttributeComparison;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonAttributeBuilder;

public class CollectionAttributeBuilder extends ComparisonAttributeBuilder {

	@Override
	protected AttributeComparison createOperation() {
		CollectionAttributeComparison comparison = new CollectionAttributeComparison();
		comparison.setAttribute(getAttribute());
	
		EntityOperationSelector<Comparison> selector = createEntityOperation();
		if (selector != null) {
			comparison.setSelector(selector);
		}
		return comparison;
	}

}
