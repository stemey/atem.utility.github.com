/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;

/**
* basic builder to create a transformation between n source attributes and m target attributes.
*  
*
*/
public interface AttributeTransformationBuilder<A, B, T extends AttributeTransformationBuilder<A,B,T>> {

  /**
* specify a source attribute
*/
	T from(String attributePath);

/**
* if the source attribute is a java method then use this to dynamically create the attribute code.
*/
	A fromMethod();
/**
* TODO this only makes sense for single target attributes.
* add meta data to the target attribute.
* @param the meta attribute name
* @param the meta data 
*/
	T metaValue(String name, Object metaData);

/**
* this is not part of te api but is called by the TypeTransformationBuilder.
*/
	void build(EntityTypeBuilder targetTypeBuilder);
/**
* this is not part of the public api but is called by the TypeTransformationBuilder.
*/
	AttributeTransformation<A,B> create(EntityType<B> targetType);

}
