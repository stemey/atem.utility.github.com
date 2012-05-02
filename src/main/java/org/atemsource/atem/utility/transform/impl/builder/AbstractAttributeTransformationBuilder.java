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
package org.atemsource.atem.utility.transform.impl.builder;


import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;


public class AbstractAttributeTransformationBuilder implements AttributeTransformationBuilder
{

	protected Converter<?, ?> converter;

	protected EntityType<?> sourceType;

	@Inject
	protected AttributePathBuilderFactory attributePathBuilderFactory;

	protected String sourceAttribute;

	protected String targetAttribute;

	protected Map<String, Object> meta = new HashMap<String, Object>();

	public AbstractAttributeTransformationBuilder()
	{
		super();
	}

	public AttributeTransformationBuilder convert(Converter<?, ?> converter)
	{
		this.converter = converter;
		return this;
	}

	@Override
	public AttributeTransformationBuilder from(String sourceAttribute)
	{
		this.sourceAttribute = sourceAttribute;
		return this;
	}

	@Override
	public AttributeTransformationBuilder metaValue(String name, Object metaData)
	{
		this.meta.put(name, metaData);
		return this;
	}

	public void setSourceType(EntityType<?> sourceType)
	{
		this.sourceType = sourceType;
	}

	@Override
	public AttributeTransformationBuilder to(String targetAttribute)
	{
		this.targetAttribute = targetAttribute;
		return this;
	}

}
