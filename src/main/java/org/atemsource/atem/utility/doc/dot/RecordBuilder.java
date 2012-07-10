package org.atemsource.atem.utility.doc.dot;

public class RecordBuilder
{

	private String label;

	private String port;

	public void create(StringBuilder builder)
	{
		if (port != null)
		{
			builder.append("<");
			builder.append("port");
			builder.append(">+ ");
		}
		builder.append(label);
	}

	public void label(String label)
	{
		this.label = label;
	}

	public void port(String port)
	{
		this.port = port;
	}

}
