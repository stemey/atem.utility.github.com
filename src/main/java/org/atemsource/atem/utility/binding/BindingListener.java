package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

/**
* Listener for events in the binding process.
*/
public interface BindingListener {
	/**
* a transformation is created. 
*/
	void finished(EntityTypeTransformation<?, ?> transformation);
}
