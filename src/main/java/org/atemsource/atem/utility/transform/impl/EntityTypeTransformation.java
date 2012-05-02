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
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class EntityTypeTransformation<A, B> extends EntityTransformation implements Transformation<A, B>
{
	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityType<A> entityTypeA;

	private EntityType<B> entityTypeB;

	public A createA(B b)
	{
		entityTypeB = entityTypeRepository.getEntityType(b);
		A valueA = (A) getTypeConverter().getBA().convert(b, null);
		transformBAChildren(b, valueA);
		return valueA;
	}

	public B createB(A a)
	{
		if (a == null)
		{
			return null;
		}
		entityTypeA = entityTypeRepository.getEntityType(a);
		B valueB = (B) getTypeConverter().getAB().convert(a, null);
		transformABChildren(a, valueB);
		return valueB;
	}

	@Override
	public UniTransformation<A, B> getAB()
	{
		return new UniTransformation<A, B>()
		{

			@Override
			public B convert(A a, Type<?> typeB)
			{
				return createB(a);
			}

			@Override
			public Type<A> getSourceType()
			{
				return entityTypeA;
			}

			@Override
			public Type<B> getTargetType()
			{
				return entityTypeB;
			}

			@Override
			public B merge(A a, B b, Type<B> typeB)
			{
				transformABChildren(a, b);
				return b;
			}
		};
	}

	@Override
	public UniTransformation<B, A> getBA()
	{
		return new UniTransformation<B, A>()
		{

			@Override
			public A convert(B b, Type<?> typeB)
			{
				return createA(b);
			}

			@Override
			public Type<B> getSourceType()
			{
				return entityTypeB;
			}

			@Override
			public Type<A> getTargetType()
			{
				return entityTypeA;
			}

			@Override
			public A merge(B b, A a, Type<A> typeB)
			{
				transformBAChildren(b, a);
				return a;
			}

		};
	}

	public EntityType<A> getEntityTypeA()
	{
		return entityTypeA;
	}

	public EntityType<B> getEntityTypeB()
	{
		return entityTypeB;
	}

	@Override
	public Type getTypeA()
	{
		return getEntityTypeA();
	}

	@Override
	public Type getTypeB()
	{
		return getEntityTypeB();
	}

	public void setEntityTypeA(EntityType<A> entityTypeA)
	{
		this.entityTypeA = entityTypeA;
	}

	public void setEntityTypeB(EntityType<B> entityTypeB)
	{
		this.entityTypeB = entityTypeB;
	}

}
