package org.atemsource.atem.utility.transform.api.meta;

public class Binding
{
	public static final String META_ATTRIBUTE_CODE = "binding";
	private String externalTypeCode;

	private String version;

	public String getExternalTypeCode()
	{
		return externalTypeCode;
	}

	public String getVersion()
	{
		return version;
	}

	public void setExternalTypeCode(String externalTypeCode)
	{
		this.externalTypeCode = externalTypeCode;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

}
