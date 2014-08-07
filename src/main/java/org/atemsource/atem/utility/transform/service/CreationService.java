package org.atemsource.atem.utility.transform.service;

import org.atemsource.atem.api.type.EntityType;

public interface CreationService<O, T> {
	public O create(EntityType<O> entityType, EntityType<T> jsonType,T bean);
}
