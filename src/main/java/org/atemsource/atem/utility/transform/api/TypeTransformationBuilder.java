/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.type.EntityType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This builder offers methods to build an EntityTypeTransformation from type A
 * to type B.
 */
@Component
@Scope("prototype")
public class TypeTransformationBuilder<A, B> extends
		AbstractTypeTransformationBuilder<A, B> {

	final static Logger logger = Logger
			.getLogger(TypeTransformationBuilder.class);

	EntityType<A> sourceType;

	public TypeTransformationBuilder() {
	}

	public void setSourceType(Class sourceType) {
		this.sourceType = entityTypeRepository.getEntityType(sourceType);
		if (this.sourceType == null) {
			throw new IllegalArgumentException(
					"source type is not an atem type " + sourceType.getName());
		}
	}

	public void setSourceType(EntityType sourceType) {
		this.sourceType = sourceType;
	}

	@Override
	protected EntityType<A> getSourceType() {
		return sourceType;
	}
}
