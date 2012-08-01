package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.type.EntityType;

public interface TypeFilter {
	public boolean isExcluded(EntityType<?> entityType);
}
