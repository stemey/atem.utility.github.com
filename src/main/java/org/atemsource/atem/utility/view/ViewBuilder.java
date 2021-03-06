/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.view;

import java.util.Set;
import javax.inject.Inject;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.common.EntityTypeOperationBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class ViewBuilder extends
	EntityTypeOperationBuilder<ViewAttributeBuilder, ViewBuilder, StandardView, AttributeViewing>
{
	@Inject
	private ViewBuilderFactory viewBuilderFactory;

	@Override
	protected ViewAttributeBuilder createAttributeOperationBuilder(Attribute attribute)
	{
		return new ViewAttributeBuilder();
	}

	@Override
	protected StandardView createInternally()
	{
		Set<AttributeViewing> operations = createOperations();
		StandardView view = new StandardView(getEntityType());
		view.setAttributeOperations(operations);
		view.setSubOperations(getSubOperations());
		view.setSuperOperation(getSuperOperation());
		return view;
	}

	@Override
	protected ViewBuilder createViewBuilder(EntityType<?> entityType)
	{
		return viewBuilderFactory.create(entityType);
	}

	@Override
	protected void setEntityType(EntityType<?> entityType)
	{
		super.setEntityType(entityType);
	}

}
