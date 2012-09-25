/*******************************************************************************
 * Stefan Meyer, 2012 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package org.atemsource.atem.utility.transform.impl;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.extension.MetaAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.DerivedAttribute;
import org.atemsource.atem.utility.transform.api.DerivedType;


public class DerivationMetaAttributeRegistrar
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
		metaAttributeService.addSingleMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE, entityTypeRepository.getEntityType(Attribute.class),
			entityTypeRepository.getEntityType(DerivedAttribute.class));
		metaAttributeService.addSingleMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE, entityTypeRepository.getEntityType(EntityType.class),
			entityTypeRepository.getEntityType(DerivedType.class));

	}

	public void setMetaAttributeService(MetaAttributeService metaAttributeService)
	{
		this.metaAttributeService = metaAttributeService;
	}

}
