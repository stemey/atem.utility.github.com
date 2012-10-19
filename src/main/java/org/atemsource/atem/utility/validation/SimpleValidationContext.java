package org.atemsource.atem.utility.validation;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.Type;


public class SimpleValidationContext extends AbstractValidationContext implements ValidationContext
{

	private final EntityTypeRepository entityTypeRepository;

	public SimpleValidationContext(EntityTypeRepository entityTypeRepository)
	{
		super();
		this.entityTypeRepository = entityTypeRepository;
	}

	@Override
	public <T> Type<T> getEntityType(T value)
	{
		return entityTypeRepository.getType(value);
	}

}
