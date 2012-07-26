package org.atemsource.atem.utility.transform.impl.binding;

import org.atemsource.atem.utility.transform.api.JavaUniConverter;


public class SimpleTypeNameConverter implements JavaUniConverter<String, String>
{
	private String prefix;

	public SimpleTypeNameConverter(String prefix)
	{
		super();
		this.prefix = prefix;
	}

	@Override
	public String convert(String a)
	{
		return prefix + ":" + a;
	}

}
