package org.atemsource.atem.utility.jackson;

import org.atemsource.atem.utility.transform.api.JavaUniConverter;


public class TypeNameConverter implements JavaUniConverter<String, String>
{

	private String prefix;

	public TypeNameConverter(String... prefixes)
	{
		super();
		prefix = "";
		for (String prefixPart : prefixes)
		{
			if (prefixPart != null)
			{
				prefix += prefixPart + ":";
			}
		}
	}

	@Override
	public String convert(String a)
	{
		return prefix + a;
	}
}
