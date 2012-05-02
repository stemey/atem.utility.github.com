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

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.impl.infrastructure.BeanLocator;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class CompareContext
{
	@Inject
	protected AttributePathBuilderFactory factory;

	@Inject
	protected BeanLocator beanLocator;

	private AttributePath path;

	public Difference addAddition(Object newValue)
	{
		Addition addition = new Addition();
		addition.setPath(path);
		addition.setValue(newValue);
		return addition;
	}

	public Difference addAttributeChange(Object oldValue, Object newValue)
	{
		AttributeChange attributeChange = new AttributeChange(path, oldValue, newValue);
		return attributeChange;
	}

	public Difference addMotion(int oldIndex, int newIndex, Object value)
	{
		Rearrangement rearragement = new Rearrangement();
		rearragement.setNewIndex(newIndex);
		rearragement.setOldIndex(oldIndex);
		rearragement.setNewPath(factory.createBuilder(path).addIndex(newIndex).createPath());
		rearragement.setOldPath(factory.createBuilder(path).addIndex(oldIndex).createPath());
		rearragement.setPath(path);
		rearragement.setValue(value);
		return rearragement;
	}

	public Difference addRemoval(Object oldValue)
	{
		Removal removal = new Removal();
		removal.setPath(path);
		removal.setValue(oldValue);
		return removal;
	}

	protected AttributePathBuilder createAttributePathBuilder()
	{
		if (path == null)
		{
			return factory.createBuilder();
		}
		else
		{
			return factory.createBuilder(path);
		}
	}

	public CompareContext createChild(Attribute<?, ?> attribute)
	{
		AttributePath childPath = createAttributePathBuilder().addAttribute(attribute).createPath();
		CompareContext newCompareContext = beanLocator.getInstance(CompareContext.class);
		newCompareContext.path = childPath;
		return newCompareContext;
	}

	public CompareContext createIndexedChild(int index)
	{
		AttributePath childPath = createAttributePathBuilder().addIndex(index).createPath();
		CompareContext newCompareContext = beanLocator.getInstance(CompareContext.class);
		newCompareContext.path = childPath;
		return newCompareContext;
	}

	public CompareContext createMapChild(Object key)
	{
		AttributePath childPath = createAttributePathBuilder().addMapKey(key).createPath();
		CompareContext newCompareContext = beanLocator.getInstance(CompareContext.class);
		newCompareContext.path = childPath;
		return newCompareContext;
	}

	public AttributePath getPath()
	{
		return path;
	}

	protected void setPath(AttributePath path)
	{
		this.path = path;
	}

}
