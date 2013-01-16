/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.constraint;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.extension.EntityTypeRepositoryPostProcessor;
import org.atemsource.atem.api.extension.MetaAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.spi.EntityTypeCreationContext;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;

/**
* This registrar registers the meta attribute for dateformat and possible values.
*/
public class ConstraintMetaAttributeRegistrar implements EntityTypeRepositoryPostProcessor
{

	private MetaAttributeService metaAttributeService;

	public MetaAttributeService getMetaAttributeService()
	{
		return metaAttributeService;
	}

	@Override
	public void initialize(EntityTypeCreationContext entityTypeCreationContext)
	{
		EntityType<?> attributeType = entityTypeCreationContext.getEntityTypeReference(Attribute.class);
		metaAttributeService.addSingleMetaAttribute(PossibleValues.META_ATTRIBUTE_CODE, attributeType,
			entityTypeCreationContext.getEntityTypeReference(PossibleValues.class));
		metaAttributeService.addSingleMetaAttribute(DateFormat.META_ATTRIBUTE_CODE, attributeType,
			entityTypeCreationContext.getEntityTypeReference(DateFormat.class));
	}

	public void setMetaAttributeService(MetaAttributeService metaAttributeService)
	{
		this.metaAttributeService = metaAttributeService;
	}

}
