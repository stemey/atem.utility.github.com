package org.atemsource.atem.utility.transform.api.constraint;

public interface PossibleValues<V> {
	public static final String META_ATTRIBUTE_CODE = "org.atemsource.atem.utility.transform.api.constraint.PossibleValues";

	public V[] getValues();
}
