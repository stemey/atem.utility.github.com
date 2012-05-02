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


import javax.annotation.PostConstruct;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.infrastructure.BeanLocator;
import org.atemsource.atem.impl.infrastructure.ReflectionUtils;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.UniConverter;


public abstract class JavaTypeConverter<A, B> implements Converter<A, B>
{

	private Type<A> typeA;

	private Type<B> typeB;

	private EntityTypeRepository entityTypeRepository;

	@Override
	public UniConverter<A, B> getAB()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UniConverter<B, A> getBA()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Type<A> getTypeA()
	{
		return typeA;
	}

	public Type<B> getTypeB()
	{
		return typeB;
	}

	@PostConstruct
	public void initialize()
	{
		entityTypeRepository = BeanLocator.getInstance().getInstance(EntityTypeRepository.class);
		Class[] actualTypeParameters = ReflectionUtils.getActualTypeParameters(getClass(), Converter.class);
		typeA = entityTypeRepository.getType(actualTypeParameters[0]);
		typeB = entityTypeRepository.getType(actualTypeParameters[1]);
	}
}
