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
package org.atemsource.atem.utility.clone;

import org.atemsource.atem.utility.common.EntityOperation;


public class Cloning extends EntityOperation<AttributeCloning>
{
	public <O> O clone(O entity)
	{
		return clone(entity, new CloningContext());
	}

	public <O> O clone(O entity, CloningContext ctx)
	{
		O clone = (O) getEntityType().createEntity();
		for (AttributeCloning attributeCloning : getAttributeOperations())
		{
			attributeCloning.clone(entity, clone, ctx);
		}
		return clone;
	}
}
