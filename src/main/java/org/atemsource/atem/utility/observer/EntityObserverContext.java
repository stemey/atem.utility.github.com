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
package org.atemsource.atem.utility.observer;


import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.compare.CompareContext;
import org.atemsource.atem.utility.path.AttributePath;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class EntityObserverContext extends CompareContext
{

	private EntityType<?> entityType;

	@Override
	public CompareContext createChild(Attribute<?, ?> attribute)
	{
		Attribute<?, ?> targetAttribute = entityType.getAttribute(attribute.getCode());
		AttributePath childPath = createAttributePathBuilder().addAttribute(targetAttribute).createPath();
		EntityObserverContext newCompareContext = beanLocator.getInstance(EntityObserverContext.class);
		newCompareContext.setPath(childPath);
		if (targetAttribute.getTargetType() instanceof EntityType<?>)
		{
			newCompareContext.setEntityType((EntityType<?>) targetAttribute.getTargetType());
		}
		return newCompareContext;
	}

	public CompareContext createIndexedChild(int index)
	{
		AttributePath childPath = createAttributePathBuilder().addIndex(index).createPath();
		EntityObserverContext newCompareContext = beanLocator.getInstance(EntityObserverContext.class);
		newCompareContext.setPath(childPath);
		newCompareContext.setEntityType(entityType);
		return newCompareContext;
	}

	public CompareContext createMapChild(Object key)
	{
		AttributePath childPath = createAttributePathBuilder().addMapKey(key).createPath();
		EntityObserverContext newCompareContext = beanLocator.getInstance(EntityObserverContext.class);
		newCompareContext.setPath(childPath);
		newCompareContext.setEntityType(entityType);
		return newCompareContext;
	}

	protected EntityType<?> getEntityType()
	{
		return entityType;
	}

	protected void setEntityType(EntityType<?> entityType)
	{
		this.entityType = entityType;
	}

}
