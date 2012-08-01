package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.EntityType;

public interface TypeNameConverter {

	String convert(EntityType<?> entityType);

}
