package org.atemsource.atem.utility.transform.api.constraint;

public interface Constraint {
	public boolean isValid(Object value);

	public String getMessage();
}
