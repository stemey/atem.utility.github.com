package org.atemsource.atem.utility.jackson;

import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.codehaus.jackson.node.ObjectNode;


public class TypeNameConverter implements JavaUniConverter<String, String>
{

	private String paket;

	private String prefix;

	private String version;

	public TypeNameConverter(String prefix, String version, String paket)
	{
		super();
		this.prefix = prefix;
		this.version = version;
		this.paket = paket;
	}

	@Override
	public String convert(String a)
	{
		if (a.equals(ObjectNode.class.getName()))
		{
			return a;
		}
		int index = a.lastIndexOf('.');
		if (paket == null)
		{
			return prefix + ":" + version + ":" + a;
		}
		else
		{
			return prefix + ":" + version + ":" + paket + ":" + a.substring(index + 1);
		}

	}

	public String getPaket()
	{
		return paket;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public String getVersion()
	{
		return version;
	}

	public void setPaket(String paket)
	{
		this.paket = paket;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

}
