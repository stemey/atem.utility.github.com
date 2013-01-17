package org.atemsource.atem.utility.transform.api.meta;

/**
* this class defines extra information a derived type. The external type code that is used to communicate type information to external systems. And the version of this derived type. There maybe different versions of derived types for the same original type. 
*/
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
