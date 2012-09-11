package org.atemsource.atem.utility.validation;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.extension.MetaAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;

public class ValidationAttributePostProcessor{
@Inject
private EntityTypeRepository entityTypeRepository;
@Inject
private MetaAttributeService metaAttributeService;
public void initialize() {
		Type<String> type = entityTypeRepository.getType(String.class);
		EntityType<Type<String>> entityType = entityTypeRepository.getEntityType(type);
	}

}
