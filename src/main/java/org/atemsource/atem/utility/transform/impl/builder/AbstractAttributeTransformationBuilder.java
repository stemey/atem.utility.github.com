/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.builder;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import net.sf.cglib.proxy.Enhancer;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePathBuilder;
import org.atemsource.atem.utility.path.AttributePathBuilderFactory;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;
import org.springframework.beans.factory.support.CglibSubclassingInstantiationStrategy;


public class AbstractAttributeTransformationBuilder<A,B> implements AttributeTransformationBuilder<A,B>
{

	protected Converter<A,B> converter;

	protected EntityType<A> sourceType;

	@Inject
	protected BeanLocator beanLocator;
	
	
	@Inject
	protected AttributePathBuilderFactory attributePathBuilderFactory;

	protected String sourceAttribute;

	protected String targetAttribute;

	protected Map<String, Object> meta = new HashMap<String, Object>();

	private AttributePathBuilder attributePathBuilder;

	public AbstractAttributeTransformationBuilder()
	{
		super();
	}

	@Override
	public AttributeTransformationBuilder<A,B> convert(Converter<A,B> converter)
	{
		this.converter = converter;
		return this;
	}

	@Override
	public AttributeTransformationBuilder<A,B> from(String sourceAttribute)
	{
		this.sourceAttribute = sourceAttribute;
		return this;
	}

	@Override
	public A fromMethod()
	{
		Enhancer enhancer = new Enhancer(); 
		return (A) enhancer.create(sourceType.getJavaType(), new PathRecorder(this));
	}

	@Override
	public AttributeTransformationBuilder<A,B> metaValue(String name, Object metaData)
	{
		this.meta.put(name, metaData);
		return this;
	}

	public void setSourceType(EntityType<A> sourceType)
	{
		this.sourceType = sourceType;
	}

	@Override
	public AttributeTransformationBuilder<A,B> to(String targetAttribute)
	{
		this.targetAttribute = targetAttribute;
		return this;
	}

	public AttributePathBuilder getSourcePathBuilder() {
		if (attributePathBuilder==null) {
			attributePathBuilder=attributePathBuilderFactory.createBuilder();
			attributePathBuilder.start(null, sourceType);
		}
		return attributePathBuilder;
	}

}
