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
package org.atemsource.atem.utility.path;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.path.AttributePath;
import org.atemsource.atem.api.path.AttributePathFactory;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.dynamic.DynamicEntity;

public class AttributePathBuilderFactory implements AttributePathFactory {

	@Inject
	private BeanLocator beanLocator;

	@Inject
	private EntityTypeRepository  entityTypeRepository;

	public AttributePath create(String path, EntityType<?> baseType) {
		AttributePathBuilder builder = beanLocator
				.getInstance(AttributePathBuilder.class);

		String[] pathElements = path.split("\\.");
		builder.start(pathElements[0], baseType);
		for (int index = 1; index < pathElements.length; index++) {
			String pathElement = pathElements[index];
			builder.addElement(pathElement);
		}
		return builder.createPath();
	}

	public AttributePathBuilder createBuilder() {
		AttributePathBuilder builder = beanLocator
				.getInstance(AttributePathBuilder.class);
		return builder;
	}

	public AttributePathBuilder createBuilder(AttributePath basePath) {
		AttributePathBuilder builder = beanLocator
				.getInstance(AttributePathBuilder.class);
		if (basePath != null) {
			builder.addPath(basePath);
		}
		return builder;
	}

	public AttributePath create(String path, Class<?> clazz) {
		return create(path,entityTypeRepository.getEntityType(clazz));
	}



}
