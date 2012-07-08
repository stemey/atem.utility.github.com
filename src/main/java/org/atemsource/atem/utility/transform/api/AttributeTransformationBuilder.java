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

public interface AttributeTransformationBuilder<A,B>
{
	AttributeTransformationBuilder<A,B> convert(Converter<A,B> converter);

	AttributeTransformationBuilder<A,B> from(String attributePath);

	A fromMethod();

	AttributeTransformationBuilder<A,B> metaValue(String name, Object metaData);

	AttributeTransformationBuilder<A,B> to(String attributePath);

}
