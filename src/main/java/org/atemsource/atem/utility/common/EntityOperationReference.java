package org.atemsource.atem.utility.common;

public class EntityOperationReference<O extends EntityOperation> {
	private O operation;

	public O getOperation() {
		return operation;
	}

	public void setOperation(O operation) {
		this.operation = operation;
	}
}
