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

import org.atemsource.atem.utility.transform.api.Transformation;


public abstract class EntityTransformation<A, B> extends AbstractCompositeTransformation<A, B>
{

	private Transformation<A, B> typeConverter;

	public Transformation<A, B> getTypeConverter()
	{
		return typeConverter;
	}

	public void setTypeConverter(Transformation<A, B> typeConverter)
	{
		this.typeConverter = typeConverter;
	}

}
