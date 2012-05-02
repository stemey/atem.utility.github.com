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
package org.atemsource.atem.utility.transform.impl.transformation;


import java.util.Map;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniConverter;
import org.atemsource.atem.utility.transform.api.UniTransformation;


public abstract class AbstractAttributeTransformation<A, B> implements Transformation<A, B>
{

	private Map<String, Object> meta;

	private AttributePath attributeA;

	private AttributePath attributeB;

	private Converter converter;

	@Inject
	protected EntityTypeRepository entityTypeRepository;

	private EntityType typeA;

	private EntityType typeB;

	public AbstractAttributeTransformation()
	{
		super();
	}

	public UniTransformation<A, B> getAB()
	{
		return new UniTransformation<A, B>()
		{

			@Override
			public B convert(A a, Type<?> typeB)
			{
				if (AbstractAttributeTransformation.this.typeB instanceof EntityType)
				{
					B b = ((EntityType<B>) AbstractAttributeTransformation.this.typeB).createEntity();
					return merge(a, b, AbstractAttributeTransformation.this.typeB);
				}
				else
				{
					throw new IllegalArgumentException("cannot handle type");
				}
			}

			@Override
			public Type<A> getSourceType()
			{
				return typeA;
			}

			@Override
			public Type<B> getTargetType()
			{
				return typeB;
			}

			@Override
			public B merge(A a, B b, Type<B> typeB)
			{
				UniConverter abConverter = converter == null ? null : converter.getAB();
				AbstractAttributeTransformation.this.transformInternally(a, b, attributeA, attributeB, abConverter);
				return b;
			}

		};
	}

	public AttributePath getAttributeA()
	{
		return attributeA;
	}

	public AttributePath getAttributeB()
	{
		return attributeB;
	}

	public UniTransformation<B, A> getBA()
	{
		return new UniTransformation<B, A>()
		{

			@Override
			public A convert(B b, Type<?> typeA)
			{
				if (AbstractAttributeTransformation.this.typeA instanceof EntityType)
				{
					A a = ((EntityType<A>) AbstractAttributeTransformation.this.typeA).createEntity();
					return merge(b, a, AbstractAttributeTransformation.this.typeA);
				}
				else
				{
					throw new IllegalArgumentException("cannot handle type");
				}
			}

			@Override
			public Type<B> getSourceType()
			{
				return typeB;
			}

			@Override
			public Type<A> getTargetType()
			{
				return typeB;
			}

			@Override
			public A merge(B b, A a, Type<A> typeA)
			{
				UniConverter baConverter = converter == null ? null : converter.getBA();
				AbstractAttributeTransformation.this.transformInternally(b, a, attributeB, attributeA, baConverter);
				return a;
			}
		};
	}

	public Converter getConverter()
	{
		return converter;
	}

	public Map<String, Object> getMeta()
	{
		return meta;
	}

	public EntityType getTypeA()
	{
		return typeA;
	}

	public EntityType getTypeB()
	{
		return typeB;
	}

	public void setAttributeA(AttributePath source)
	{
		this.attributeA = source;
	}

	public void setAttributeB(AttributePath attributeB)
	{
		this.attributeB = attributeB;
	}

	public void setConverter(Converter converter)
	{
		this.converter = converter;
	}

	public void setMeta(Map<String, Object> meta)
	{
		this.meta = meta;
	}

	public void setTypeA(EntityType typeA)
	{
		this.typeA = typeA;
	}

	public void setTypeB(EntityType typeB)
	{
		this.typeB = typeB;
	}

	protected abstract void transformInternally(Object a, Object b, AttributePath attributeA, AttributePath attributeB,
		UniConverter<Object, Object> ab);
}
