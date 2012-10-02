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
package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.impl.meta.DerivedObject;


public class DerivedAttribute implements DerivedObject
{

	private AttributeTransformation<?, ?> transformation;

	private Attribute<?, ?> originalAttribute;

	public Attribute<?, ?> getOriginalAttribute()
	{
		return originalAttribute;
	}

	public AttributeTransformation<?, ?> getTransformation()
	{
		return transformation;
	}

	public void setOriginalAttribute(Attribute<?, ?> originalAttribute)
	{
		this.originalAttribute = originalAttribute;
	}

	public void setTransformation(AttributeTransformation<?, ?> transformation)
	{
		this.transformation = transformation;
	}

	@Override
	public Object getOriginal() {
		return originalAttribute;
	}

}
