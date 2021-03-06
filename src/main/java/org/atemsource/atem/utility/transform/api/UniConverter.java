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

import org.atemsource.atem.api.type.Type;

/**
* The general interface for unidirectional conversions.
*
*/
public interface UniConverter<A, B>
{
	/**
	* 
	 * @param a the value to transform
	 * @param ctx context
	 * @return the transformed instance
	 */
	B convert(A a, TransformationContext ctx);

	
	Type<A> getSourceType();

	Type<B> getTargetType();
	
/**
* If the actual type of the object to be transformed is a subtype of the ype returned by getSourceType, then the target type might differ from the type returned by getTargetType.
* @param the actual sourceType
* @return the actual targetType based on the actual source type
*/
	Type<? extends B> getTargetType(Type<? extends A> sourceType);

}
