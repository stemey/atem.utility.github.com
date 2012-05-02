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
package org.atemsource.atem.utility.transform.impl;


import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.utility.path.AttributePath;


public abstract class AbstractAssociationAttributeTransformation<A, B, C, D> extends EntityTransformation<C, D>
{

	protected AttributePath attributeCodeA;

	protected AttributePath attributeCodeB;

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	public AbstractAssociationAttributeTransformation()
	{
		super();
	}

	public AttributePath getAttributeCodeA()
	{
		return attributeCodeA;
	}

	public AttributePath getAttributeCodeB()
	{
		return attributeCodeB;
	}

	public void setAttributeCodeA(AttributePath sourcePath)
	{
		this.attributeCodeA = sourcePath;

	}

	public void setAttributeCodeB(AttributePath targetPath)
	{
		this.attributeCodeB = targetPath;

	}

}
