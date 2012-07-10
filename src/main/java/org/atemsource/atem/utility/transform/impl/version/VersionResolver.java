package org.atemsource.atem.utility.transform.impl.version;

import org.atemsource.atem.utility.transform.api.annotation.Versions;


public class VersionResolver
{

	private int parts;

	private String separator = ".";

	public VersionResolver()
	{
		super();
	}

	public VersionResolver(int parts, String separator)
	{
		super();
		this.parts = parts;
		this.separator = separator;
	}

	public VersionNumber create(String version)
	{
		if (version.equals(Versions.MAX))
		{
			return null;
		}
		else if (version.equals(Versions.MIN))
		{
			return null;
		}
		String[] split = version.split(separator);
		if (split.length > parts)
		{
			throw new IllegalArgumentException(" version is malformed " + version);
		}
		int[] versions = new int[parts];
		for (int index = 0; index < parts; index++)
		{
			if (split.length > index)
			{
				int versionInt = Integer.parseInt(split[index]);
				versions[index] = versionInt;
			}
			else
			{
				versions[index] = 0;
			}
		}
		return new VersionNumber(versions);
	}

	public int getParts()
	{
		return parts;
	}

	public String getSeparator()
	{
		return separator;
	}

	public void setParts(int parts)
	{
		this.parts = parts;
	}

	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

}
