package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public interface BindingListener {
	void finished(EntityTypeTransformation<?, ?> transformation);
}
