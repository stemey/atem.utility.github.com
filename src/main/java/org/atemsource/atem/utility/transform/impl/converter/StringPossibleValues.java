package org.atemsource.atem.utility.transform.impl.converter;

import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;


public class StringPossibleValues implements PossibleValues
{
	private final String[] values;

	public StringPossibleValues(String[] values)
	{
		this.values = values;
	}

	@Override
	public String[] getValues()
	{
		return values;
	}
}
