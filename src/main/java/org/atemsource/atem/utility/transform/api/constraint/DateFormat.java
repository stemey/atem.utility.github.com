package org.atemsource.atem.utility.transform.api.constraint;

public class DateFormat
{
	public static final String META_ATTRIBUTE_CODE = DateFormat.class.getName().replace('.', '_');

	private String pattern;

	public DateFormat()
	{
		super();
	}

	public DateFormat(String pattern)
	{
		super();
		this.pattern = pattern;
	}

	public String getPattern()
	{
		return pattern;
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}
}
