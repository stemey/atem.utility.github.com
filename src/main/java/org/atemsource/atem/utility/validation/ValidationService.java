package org.atemsource.atem.utility.validation;

import org.atemsource.atem.api.type.EntityType;

public interface ValidationService {

	public abstract <J> void validate(EntityType<J> entityType, ValidationContext context, J entity);

}
