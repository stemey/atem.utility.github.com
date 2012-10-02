package org.atemsource.atem.utility.transform.impl.converter;

public interface Constraining {
	public String[] getConstraintNamesAB();

	public Object getConstraintAB(String name);
	public String[] getConstraintNamesBA();

	public Object getConstraintBA(String name);
}
