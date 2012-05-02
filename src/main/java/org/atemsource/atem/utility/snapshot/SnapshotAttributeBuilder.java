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
package org.atemsource.atem.utility.snapshot;


import javax.inject.Inject;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.AttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class SnapshotAttributeBuilder
{
	private AttributeTransformationBuilder builder;

	private boolean useId;

	@Inject
	private SnapshotBuilderFactory snapshotBuilderFactory;

	private SnapshotBuilder subBuilder;

	private Type<?> targetType;

	public SnapshotBuilder cascade()
	{
		subBuilder = snapshotBuilderFactory.create((EntityType<?>) targetType);
		return subBuilder;
	}

	public void cascade(Converter<?, ?> converter)
	{
		builder.convert(converter);
	}

	public void initialize(AttributeTransformationBuilder builder, Type<?> targetType)
	{
		this.builder = builder;
		this.targetType = targetType;
	}

	public void preCreate()
	{
		if (subBuilder != null)
		{
			Transformation<?, ?> converter = subBuilder.create();
			builder.convert(converter);
		}
	}

	public SnapshotAttributeBuilder useId()
	{
		this.useId = true;
		return this;
	}

}
