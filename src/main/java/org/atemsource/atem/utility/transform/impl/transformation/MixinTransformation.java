/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import java.util.HashSet;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class MixinTransformation<A, B> extends AbstractAttributeTransformation<A, B>
{

	private EntityTypeTransformation<Object, Object> transformation;


	public EntityTypeTransformation<Object, Object> getTransformation()
	{
		return transformation;
	}

	@Override
	public void mergeAB(A a, B b, TransformationContext ctx)
	{
			transformation.getAB().merge(a, b, ctx);
	}

	@Override
	public void mergeBA(B b, A a, TransformationContext ctx)
	{
		transformation.getBA().merge(b, a, ctx);
	}


	public void setTransformation(EntityTypeTransformation<Object, Object> transformation)
	{
		this.transformation = transformation;
	}

}
