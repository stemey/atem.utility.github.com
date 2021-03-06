/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.snapshot;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;

public class SnapshotBuilderFactory {

	private DynamicEntityTypeSubrepository<?> dynamicEntityTypeRepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	public TransformationBuilderFactory getTransformationBuilderFactory() {
		return transformationBuilderFactory;
	}

	public void setTransformationBuilderFactory(
			TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public AttributePathBuilderFactory getAttributePathBuilderFactory() {
		return attributePathBuilderFactory;
	}

	public void setAttributePathBuilderFactory(
			AttributePathBuilderFactory attributePathBuilderFactory) {
		this.attributePathBuilderFactory = attributePathBuilderFactory;
	}

	private AttributePathBuilderFactory attributePathBuilderFactory;

	@Inject
	protected BeanLocator beanLocator;

	public SnapshotBuilder create(EntityType<?> entityType) {
		SnapshotBuilder builder = beanLocator
				.getInstance(SnapshotBuilder.class);
		builder.setEntityType(entityType);
		builder.setDynamicEntityTypeRepository(dynamicEntityTypeRepository);
		builder.setTransformationBuilderFactory(transformationBuilderFactory);
		builder.setAttributePathBuilderFactory(attributePathBuilderFactory);
		builder.setFactory(this);
		builder.initialize();
		return builder;
	}

	public void setDynamicEntityTypeRepository(
			DynamicEntityTypeSubrepository<?> dynamicEntityTypeRepository) {
		this.dynamicEntityTypeRepository = dynamicEntityTypeRepository;
	}
}
