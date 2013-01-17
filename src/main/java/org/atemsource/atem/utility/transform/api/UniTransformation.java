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


/**
* This class extends UniConverter. The UniTransformation does not transform immutables (like most primitives). 
*
*/
public interface UniTransformation<A, B> extends UniConverter<A, B>
{

	/**

	 * @param a the value to transform
	 * @param b the value to be transformed. If the type needs to be adjusted than a different instance is returned
	 * @return the transfomed value, if no type change is necessary this will be the same instance as parameter b
	 */
	public B merge(A a, B b, TransformationContext newParam);

}
