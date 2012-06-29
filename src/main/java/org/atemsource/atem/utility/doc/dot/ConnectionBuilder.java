package org.atemsource.atem.utility.doc.dot;

public class ConnectionBuilder
{
	private String label;

	private String source;

	private String target;

	public void create(StringBuilder builder)
	{
		builder.append(source);
		builder.append(" -> ");
		builder.append(target);
		builder.append(" [label=\"");
		builder.append(label);
		builder.append("\"]\r\n");
	}

	public void label(String label)
	{
		this.label = label;
	}

	public void source(String source)
	{
		this.source = source;
	}

	public void target(String target)
	{
		this.target = target;

	}

}
