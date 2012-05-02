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
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.impl.meta.MetaAttributeService;
import org.atemsource.atem.impl.meta.SingleMetaAttribute;
import org.atemsource.atem.utility.transform.api.DerivationAttribute;


public class DerivationAttributeRegistrar
{
	@Inject
	private MetaAttributeService metaAttributeService;

	private SingleMetaAttribute<DerivationAttribute> derivationAttribute;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	public SingleMetaAttribute<DerivationAttribute> getDerivationAttribute()
	{
		return derivationAttribute;
	}

	@PostConstruct
	public void initialize()
	{
		derivationAttribute =
			metaAttributeService.addSingleMetaAttribute("derivationAttribute",
				entityTypeRepository.getEntityType(Attribute.class),
				entityTypeRepository.getEntityType(DerivationAttribute.class));

	}

}
