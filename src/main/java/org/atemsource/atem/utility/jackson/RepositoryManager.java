package org.atemsource.atem.utility.jackson;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.DerivedType;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;


public interface RepositoryManager
{

	TypeTransformationBuilder<?, ?> createTransformationBuilder(EntityType<?> entityType);

	DerivedType getDerivedType(EntityType<?> targetType);

	EntityType getEntityTypeReference(EntityType<?> originalType);

	JavaUniConverter<String, String> getTypeNameConverter();

	void onTypeCreated(EntityType<?> entityType);

}
