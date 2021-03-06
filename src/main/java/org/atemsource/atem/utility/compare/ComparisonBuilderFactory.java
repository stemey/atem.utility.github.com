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
package org.atemsource.atem.utility.compare;

import javax.inject.Inject;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.BeanLocator;

public class ComparisonBuilderFactory {

	private String beanName;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Inject
	private BeanLocator beanLocator;

	public ComparisonBuilder create(EntityType<?> entityType) {
		ComparisonBuilder instance;

		if (beanName != null) {
			instance = beanLocator.getInstance(beanName);
		} else {
			instance = beanLocator.getInstance(ComparisonBuilder.class);
		}
		instance.setEntityType(entityType);
		return instance;
	}

}
