package org.atemsource.atem.utility.transform.api;

import org.atemsource.atem.api.type.EntityType;

/**
* The TypeNameConverter creates a name/code ( e.g. for the target type ) based on a (source) entityType.
*/
public interface TypeNameConverter {

	/**
* return the type code for the target type based on the source type.
*/
	String convert(EntityType<?> entityType);

}
