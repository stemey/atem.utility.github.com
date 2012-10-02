package org.atemsource.atem.utility.transform.api.constraint;

public interface PossibleValues
{
	public static final String META_ATTRIBUTE_CODE = PossibleValues.class.getName().replace('.', '_');

	public String[] getValues();
}
