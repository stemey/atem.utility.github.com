/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api.meta;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

/**
* This class defines the eta data to attach to a derived type. The original type and the transformation.
*/
public class DerivedType<O,T> implements DerivedObject
{

	private EntityType<O> originalType;

	private EntityTypeTransformation<O, T> transformation;

	public EntityType<O> getOriginalType()
	{
		return originalType;
	}

	public EntityTypeTransformation<O, T> getTransformation()
	{
		return transformation;
	}

	public void setOriginalType(EntityType<O> originalType)
	{
		this.originalType = originalType;
	}

	public void setTransformation(EntityTypeTransformation<O, T> transformation)
	{
		this.transformation = transformation;
	}
	
	public Attribute findDerived(Attribute originalAttribute) {
		return transformation.getDerivedAttribute(originalAttribute);
	}

	public Attribute findOriginal(Attribute derivedAttribute) {
		return transformation.getOriginalAttribute(derivedAttribute);
	}

	@Override
	public Object getOriginal() {
		return originalType;
	}

}
