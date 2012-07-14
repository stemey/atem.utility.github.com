package org.atemsource.atem.utility.common;

import java.util.HashMap;
import java.util.Map;

import org.atemsource.atem.api.type.Type;

public class EntityOperationSelector<E extends EntityOperation> {

	private E defaultOperation;
	private Map<Type<?>, E> operations;
	private Map<Type<?>, EntityOperationReference<E>> operationRefs;

	

	public void setDefaultOperation(E defaultOperation) {
		this.defaultOperation = defaultOperation;
	}

	public E select(Type<?> actualType) {
		E selected;
		if (operations != null) {
			selected = operations.get(actualType);
			if (selected != null) {
				return selected;
			}
		}
		if (operationRefs != null) {
			EntityOperationReference<E> selectedRef = operationRefs.get(actualType);
			if (selectedRef != null) {
				return selectedRef.getOperation();
			}
		}
		return defaultOperation;
	}

	public void put(Type<?> type, E operation) {
		if (operations == null) {
			operations = new HashMap<Type<?>, E>();
		}
		operations.put(type, operation);
	}

	public void put(Type<?> type, EntityOperationReference<E> ref) {
		if (operationRefs == null) {
			operationRefs = new HashMap<Type<?>, EntityOperationReference<E>>();
		}
		operationRefs.put(type, ref);
	}

	public String toString(String indent) {
		return defaultOperation.toString(indent);
	}

}
