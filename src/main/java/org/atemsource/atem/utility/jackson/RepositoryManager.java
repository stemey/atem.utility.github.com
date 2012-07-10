package org.atemsource.atem.utility.jackson;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;


public interface RepositoryManager
{

	TypeTransformationBuilder<?, ?> createTransformationBuilder(EntityType<?> entityType);

}
