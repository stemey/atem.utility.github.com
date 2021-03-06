/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.transformation;

import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.Transformation;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class CustomCollectionAssociationAttributeTransformation<A, B, C, D> implements Transformation<A, B>

{
	protected AttributePath attributeCodeA;

	protected AttributePath attributeCodeB;

	private boolean convertNullToEmpty;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private CollectionTransformation<B, C, D> groupTransformation;

	@Override
	public UniTransformation<A, B> getAB()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public AttributePath getAttributeCodeA()
	{
		return attributeCodeA;
	}

	public AttributePath getAttributeCodeB()
	{
		return attributeCodeB;
	}

	@Override
	public UniTransformation<B, A> getBA()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public CollectionTransformation<B, C, D> getGroupTransformation()
	{
		return groupTransformation;
	}

	@Override
	public Type getTypeA()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getTypeB()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isConvertNullToEmpty()
	{
		return convertNullToEmpty;
	}

	public void setAttributeCodeA(AttributePath attributeCodeA)
	{
		this.attributeCodeA = attributeCodeA;
	}

	public void setAttributeCodeB(AttributePath attributeCodeB)
	{
		this.attributeCodeB = attributeCodeB;
	}

	public void setConvertNullToEmpty(boolean convertNullToEmpty)
	{
		this.convertNullToEmpty = convertNullToEmpty;
	}

	public void setGroupTransformation(CollectionTransformation<B, C, D> groupTransformation)
	{
		this.groupTransformation = groupTransformation;
	}

	@Override
	public AbstractOneToOneAttributeTransformation<A, B> getAttributeTransformationByA(String attributeCode) {
		return null;
	}

	@Override
	public AbstractOneToOneAttributeTransformation<A, B> getAttributeTransformationByB(String attributeCode) {
		return null;
	}

}
