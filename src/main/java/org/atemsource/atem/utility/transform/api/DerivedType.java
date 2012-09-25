/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class DerivedType implements DerivedObject
{

	private EntityType<?> originalType;

	private EntityTypeTransformation<?, ?> transformation;

	public EntityType<?> getOriginalType()
	{
		return originalType;
	}

	public EntityTypeTransformation<?, ?> getTransformation()
	{
		return transformation;
	}

	public void setOriginalType(EntityType<?> originalType)
	{
		this.originalType = originalType;
	}

	public void setTransformation(EntityTypeTransformation<?, ?> transformation)
	{
		this.transformation = transformation;
	}

	@Override
	public Object getOriginal() {
		return originalType;
	}

}
