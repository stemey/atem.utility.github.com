/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.UniConverter;


public abstract class JavaTypeConverter<A, B> implements Converter<A, B>
{

	private Type<A> typeA;

	private Type<B> typeB;

	public abstract B convertAB(A a);

	public abstract A convertBA(B b);

	@Override
	public UniConverter<A, B> getAB()
	{
		return new UniConverter<A, B>() {

			@Override
			public B convert(A a)
			{
				return convertAB(a);
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
		};
	}

	@Override
	public UniConverter<B, A> getBA()
	{
		return new UniConverter<B, A>() {

			@Override
			public A convert(B a)
			{
				return convertBA(a);
			}

			@Override
			public Type<B> getSourceType()
			{
				return typeB;
			}

			@Override
			public Type<A> getTargetType()
			{
				return typeA;
			}
		};
	}

	@Override
	public Type<A> getTypeA()
	{
		return typeA;
	}

	@Override
	public Type<B> getTypeB()
	{
		return typeB;
	}

	public void JavaTypeConverter()
	{
		Class[] actualTypeParameters = ReflectionUtils.getActualTypeParameters(getClass(), JavaTypeConverter.class);
		EntityTypeRepository repository = BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
		typeA = repository.getType(actualTypeParameters[0]);
		typeB = repository.getType(actualTypeParameters[1]);
	}
}
