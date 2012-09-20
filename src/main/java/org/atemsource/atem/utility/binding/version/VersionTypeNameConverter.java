package org.atemsource.atem.utility.binding.version;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;


public class VersionTypeNameConverter implements TypeNameConverter
{
	private final String prefix;

	private final String version;

	public VersionTypeNameConverter(String prefix, String version)
	{
		super();
		this.prefix = prefix;
		this.version = version;
	}

	@Override
	public String convert(EntityType<?> type)
	{
		return prefix + ":" + version + ":" + type.getJavaType().getSimpleName();
	}

}
