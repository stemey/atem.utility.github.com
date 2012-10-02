/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl.constraint;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.extension.MetaAttributeService;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;


public class ConstraintMetaAttributeRegistrar
{

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private MetaAttributeService metaAttributeService;

	public MetaAttributeService getMetaAttributeService()
	{
		return metaAttributeService;
	}

	@PostConstruct
	public void initialize()
	{
		metaAttributeService.addSingleMetaAttribute(PossibleValues.META_ATTRIBUTE_CODE,
			entityTypeRepository.getEntityType(Attribute.class), entityTypeRepository.getEntityType(PossibleValues.class));
		metaAttributeService.addSingleMetaAttribute(DateFormat.META_ATTRIBUTE_CODE,
			entityTypeRepository.getEntityType(Attribute.class), entityTypeRepository.getEntityType(DateFormat.class));

	}

	public void setMetaAttributeService(MetaAttributeService metaAttributeService)
	{
		this.metaAttributeService = metaAttributeService;
	}

}
